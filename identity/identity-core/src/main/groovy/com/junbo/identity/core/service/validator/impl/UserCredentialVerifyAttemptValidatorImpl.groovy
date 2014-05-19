package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.UserCredentialVerifyAttemptValidator
import com.junbo.identity.core.service.validator.UsernameValidator
import com.junbo.identity.data.identifiable.CredentialType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.util.CollectionUtils

import java.util.regex.Pattern

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class UserCredentialVerifyAttemptValidatorImpl implements UserCredentialVerifyAttemptValidator {

    // Todo:    Refactor this code according to marshall's requirement

    private static final String MAIL_IDENTIFIER = "@"

    private UserCredentialVerifyAttemptRepository userLoginAttemptRepository
    private UserRepository userRepository
    private UserPasswordRepository userPasswordRepository
    private UserPinRepository userPinRepository
    private UserPersonalInfoRepository userPersonalInfoRepository

    private UsernameValidator usernameValidator

    private List<Pattern> allowedIpAddressPatterns
    private Integer userAgentMinLength
    private Integer userAgentMaxLength
    private Integer maxRetryCount

    private NormalizeService normalizeService

    private PlatformTransactionManager transactionManager

    private CredentialHashFactory credentialHashFactory

    @Override
    Promise<UserCredentialVerifyAttempt> validateForGet(UserCredentialVerifyAttemptId userLoginAttemptId) {
        if (userLoginAttemptId == null) {
            throw new IllegalArgumentException('userLoginAttemptId is null')
        }

        return userLoginAttemptRepository.get(userLoginAttemptId).then { UserCredentialVerifyAttempt userLoginAttempt ->
            if (userLoginAttempt == null) {
                throw AppErrors.INSTANCE.userLoginAttemptNotFound(userLoginAttemptId).exception()
            }

            return userRepository.get(userLoginAttempt.userId).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userLoginAttempt.userId).exception()
                }

                if (user.isAnonymous == true) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userLoginAttempt.userId).exception()
                }

                if (user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userLoginAttempt.userId).exception()
                }

                return Promise.pure(userLoginAttempt)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserCredentialAttemptListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt == null) {
            throw new IllegalArgumentException('userLoginAttempt is null')
        }
        checkBasicUserLoginAttemptInfo(userLoginAttempt)

        if (userLoginAttempt.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (userLoginAttempt.succeeded != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('succeeded').exception()
        }

        return findUser(userLoginAttempt).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userLoginAttempt.username).exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userLoginAttempt.username).exception()
            }

            if (user.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userLoginAttempt.username).exception()
            }

            userLoginAttempt.setUserId((UserId)user.id)

            if (userLoginAttempt.type == CredentialType.PASSWORD.toString()) {
                return userPasswordRepository.search(new UserPasswordListOptions(
                        userId: (UserId)user.id,
                        active: true
                )).then { List<UserPassword> userPasswordList ->
                    if (CollectionUtils.isEmpty(userPasswordList) || userPasswordList.size() > 1) {
                        throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
                    }

                    List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                    CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                        return hash.matches(userLoginAttempt.value, userPasswordList.get(0).passwordHash)
                    }

                    if (matched != null) {
                        userLoginAttempt.setSucceeded(true)
                    } else {
                        userLoginAttempt.setSucceeded(false)
                    }

                    return checkMaximumRetryCount(user, userLoginAttempt)
                }
            }
            else {
                return userPinRepository.search(new UserPinListOptions(
                        userId: (UserId)user.id,
                        active: true
                )).then { List<UserPin> userPinList ->
                    if (userPinList == null || userPinList.size() > 1) {
                        throw AppErrors.INSTANCE.userPinIncorrect().exception()
                    }

                    List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                    CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                        return hash.matches(userLoginAttempt.value, userPinList.get(0).pinHash)
                    }

                    if (matched != null) {
                        userLoginAttempt.setSucceeded(true)
                    } else {
                        userLoginAttempt.setSucceeded(false)
                    }

                    return checkMaximumRetryCount(user, userLoginAttempt)
                }
            }
        }
    }

    private Promise<User> findUser(UserCredentialVerifyAttempt userLoginAttempt) {
        if (isEmail(userLoginAttempt.username)) {
            return userPersonalInfoRepository.searchByEmail(userLoginAttempt.username).
                then { List<UserPersonalInfo> personalInfos ->
                    if (CollectionUtils.isEmpty(personalInfos)) {
                        throw AppErrors.INSTANCE.userNotFound(userLoginAttempt.username).exception()
                    }

                    UserPersonalInfo personalInfo = personalInfos.find { UserPersonalInfo userPersonalInfo ->
                        return userPersonalInfo.isValidated == true
                    }

                    if (personalInfo == null) {
                        throw AppErrors.INSTANCE.userNotFound(userLoginAttempt.username).exception()
                    }

                    return userRepository.get(personalInfo.userId).then { User user ->
                        if (CollectionUtils.isEmpty(user.emails)) {
                            throw AppErrors.INSTANCE.userNotFound(userLoginAttempt.username).exception()
                        }

                        if (user.emails.any { UserPersonalInfoLink link ->
                            return link.value == personalInfo.id
                            // todo:    No matter default or non-default can login
                            // && (link.isDefault == true)
                        }
                        ) {
                            return Promise.pure(user)
                        }

                        throw AppErrors.INSTANCE.fieldInvalid('username', 'Only primary mail can login').exception()
                    }
                }
        } else {
            return userRepository.getUserByCanonicalUsername(normalizeService.normalize(userLoginAttempt.username))
        }
    }

    private Promise<Void> checkMaximumRetryCount(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt.succeeded == true) {
            return Promise.pure(null)
        }

        return userLoginAttemptRepository.search(new UserCredentialAttemptListOptions(
                userId: (UserId)user.id,
                type: userLoginAttempt.type
        )).then { List<UserCredentialVerifyAttempt> attemptList ->
            if (CollectionUtils.isEmpty(attemptList) || attemptList.size() < maxRetryCount) {
                return Promise.pure(null)
            }

            attemptList.sort(new Comparator<UserCredentialVerifyAttempt>() {
                @Override
                int compare(UserCredentialVerifyAttempt o1, UserCredentialVerifyAttempt o2) {
                    return o2.createdTime <=> o1.createdTime
                }
            })

            int index = 0
            for ( ; index < maxRetryCount; index++) {
                if (attemptList.get(index).succeeded == true) {
                    break
                }
            }

            // Reach maximum count, lock account
            if (index == maxRetryCount) {
                user.status = UserStatus.SUSPEND.toString()
                return createInNewTran(user).then {
                    throw AppErrors.INSTANCE.fieldInvalid('userId',
                            'User reaches maximum allowed retry count').exception()
                }
            }

            return Promise.pure(null)
        }
    }

    private void checkBasicUserLoginAttemptInfo(UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt == null) {
            throw new IllegalArgumentException('userLoginAttempt is null')
        }

        if (userLoginAttempt.ipAddress != null) {
            if (!allowedIpAddressPatterns.any {
                        Pattern pattern -> pattern.matcher(userLoginAttempt.ipAddress).matches()
                    }) {
                throw AppErrors.INSTANCE.fieldInvalid('ipAddress').exception()
            }
        }

        if (userLoginAttempt.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }

        List<String> allowedTypes = CredentialType.values().collect { CredentialType credentialType ->
            credentialType.toString()
        }

        if (!(userLoginAttempt.type in allowedTypes)) {
            throw AppErrors.INSTANCE.fieldInvalid('type', allowedTypes.join(',')).exception()
        }

        if (userLoginAttempt.userAgent != null) {
            if (userLoginAttempt.userAgent.length() > userAgentMaxLength) {
                throw AppErrors.INSTANCE.fieldTooLong('userAgent', userAgentMaxLength).exception()
            }

            if (userLoginAttempt.userAgent.length() < userAgentMinLength) {
                throw AppErrors.INSTANCE.fieldTooShort('userAgent', userAgentMinLength).exception()
            }
        }

        if (userLoginAttempt.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
    }

    Promise<User> createInNewTran(User user) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<User>>() {
            Promise<User> doInTransaction(TransactionStatus txnStatus) {
                return userRepository.update(user)
            }
        })
    }

    private boolean isEmail(String value) {
        if (value.contains(MAIL_IDENTIFIER)) {
            return true
        }
        return false
    }

    @Required
    void setUserLoginAttemptRepository(UserCredentialVerifyAttemptRepository userLoginAttemptRepository) {
        this.userLoginAttemptRepository = userLoginAttemptRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setAllowedIpAddressPatterns(List<String> allowedIpAddressPatterns) {
        this.allowedIpAddressPatterns = allowedIpAddressPatterns.collect {
            String pattern -> Pattern.compile(pattern)
        }
    }

    @Required
    void setUserAgentMinLength(Integer userAgentMinLength) {
        this.userAgentMinLength = userAgentMinLength
    }

    @Required
    void setUserAgentMaxLength(Integer userAgentMaxLength) {
        this.userAgentMaxLength = userAgentMaxLength
    }

    @Required
    void setUsernameValidator(UsernameValidator usernameValidator) {
        this.usernameValidator = usernameValidator
    }

    @Required
    void setUserPasswordRepository(UserPasswordRepository userPasswordRepository) {
        this.userPasswordRepository = userPasswordRepository
    }

    @Required
    void setUserPinRepository(UserPinRepository userPinRepository) {
        this.userPinRepository = userPinRepository
    }

    @Required
    void setNormalizeService(NormalizeService normalizeService) {
        this.normalizeService = normalizeService
    }

    @Required
    void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount
    }

    @Required
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Required
    void setCredentialHashFactory(CredentialHashFactory credentialHashFactory) {
        this.credentialHashFactory = credentialHashFactory
    }
}
