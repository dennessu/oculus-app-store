package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.util.CipherHelper
import com.junbo.identity.core.service.validator.UserCredentialVerifyAttemptValidator
import com.junbo.identity.core.service.validator.UsernameValidator
import com.junbo.identity.data.identifiable.CredentialType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserCredentialVerifyAttemptRepository
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
class UserCredentialVerifyAttemptValidatorImpl implements UserCredentialVerifyAttemptValidator {

    private UserCredentialVerifyAttemptRepository userLoginAttemptRepository

    private UserRepository userRepository

    private UserPasswordRepository userPasswordRepository

    private UserPinRepository userPinRepository

    private UsernameValidator usernameValidator

    private List<Pattern> allowedIpAddressPatterns

    private Integer userAgentMinLength
    private Integer userAgentMaxLength

    private NormalizeService normalizeService

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

        return userRepository.getUserByCanonicalUsername(normalizeService.normalize(userLoginAttempt.username))
                .then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userLoginAttempt.username).exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userLoginAttempt.userId).exception()
            }

            userLoginAttempt.setUserId((UserId)user.id)

            def hasLen = 4
            def saltIndex = 1
            def pepperIndex = 2
            if (userLoginAttempt.type == CredentialType.PASSWORD.toString()) {
                return userPasswordRepository.search(new UserPasswordListOptions(
                        userId: (UserId)user.id,
                        active: true
                )).then { List<UserPassword> userPasswordList ->
                    if (userPasswordList == null || userPasswordList.size() > 1) {
                        throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
                    }

                    String[] hashInfo = userPasswordList.get(0).passwordHash.split(CipherHelper.COLON)
                    if (hashInfo.length != hasLen) {
                        throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
                    }

                    String salt = hashInfo[saltIndex]
                    String pepper = hashInfo[pepperIndex]

                    if (CipherHelper.generateCipherHashV1(userLoginAttempt.value, salt, pepper)
                            == userPasswordList.get(0).passwordHash) {
                        userLoginAttempt.setSucceeded(true)
                    } else {
                        userLoginAttempt.setSucceeded(false)
                    }

                    return Promise.pure(null)
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

                    String[] hashInfo = userPinList.get(0).pinHash.split(CipherHelper.COLON)
                    if (hashInfo.length != hasLen) {
                        throw AppErrors.INSTANCE.userPinIncorrect().exception()
                    }

                    String salt = hashInfo[saltIndex]
                    String pepper = hashInfo[pepperIndex]

                    if (CipherHelper.generateCipherHashV1(userLoginAttempt.value, salt, pepper)
                            == userPinList.get(0).pinHash) {
                        userLoginAttempt.setSucceeded(true)
                    } else {
                        userLoginAttempt.setSucceeded(false)
                    }

                    return Promise.pure(null)
                }
            }
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
}
