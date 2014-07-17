package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.UserCredentialVerifyAttemptValidator
import com.junbo.identity.data.identifiable.CredentialType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * Check userValid (non-anonymous and active user.)
 * Check IP patterns
 * Check userAgent minimum and maximum length
 * Check maxRetry number
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class UserCredentialVerifyAttemptValidatorImpl implements UserCredentialVerifyAttemptValidator {
    private static final String MAIL_IDENTIFIER = "@"

    private UserCredentialVerifyAttemptRepository userLoginAttemptRepository
    private UserRepository userRepository
    private UserPasswordRepository userPasswordRepository
    private UserPinRepository userPinRepository
    private UserPersonalInfoRepository userPersonalInfoRepository

    private List<Pattern> allowedIpAddressPatterns
    private Integer userAgentMinLength
    private Integer userAgentMaxLength
    // This is to check good user can't fail to login in some time
    private Integer maxRetryCount
    private Integer retryInterval

    // This is to check maxSameUserAttemptCount login attempts for the same user within attemptRetryCount time frame
    private Integer maxSameUserAttemptCount
    private Integer sameUserAttemptRetryInterval

    // This is to check More than maxSameIPRetryCount login attempts from the same IP address for different user account within sameIPRetryInterval timeframe
    private Integer maxSameIPRetryCount
    private Integer sameIPRetryInterval

    private NormalizeService normalizeService
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

                if (user.isAnonymous) {
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
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
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
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        if (userLoginAttempt.succeeded != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('succeeded').exception()
        }

        return checkMaximumSameIPAttemptCount(userLoginAttempt).then {
            return findUser(userLoginAttempt).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFoundByName(userLoginAttempt.username).exception()
                }

                if (user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatusByName(userLoginAttempt.username).exception()
                }

                if (user.isAnonymous) {
                    throw AppErrors.INSTANCE.userInInvalidStatusByName(userLoginAttempt.username).exception()
                }

                userLoginAttempt.setUserId((UserId)user.id)

                if (userLoginAttempt.type == CredentialType.PASSWORD.toString()) {
                    return userPasswordRepository.searchByUserIdAndActiveStatus((UserId)user.id, true, Integer.MAX_VALUE,
                            0).then { List<UserPassword> userPasswordList ->
                        if (CollectionUtils.isEmpty(userPasswordList) || userPasswordList.size() > 1) {
                            throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
                        }

                        List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                        CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                            return hash.matches(userLoginAttempt.value, userPasswordList.get(0).passwordHash)
                        }
                        userLoginAttempt.setSucceeded(matched != null)
                        return checkMaximumRetryCount(user, userLoginAttempt).then {
                            return checkMaximumSameUserAttemptCount(user, userLoginAttempt)
                        }
                    }
                }
                else {
                    return userPinRepository.searchByUserIdAndActiveStatus((UserId)user.id, true, Integer.MAX_VALUE,
                            0).then { List<UserPin> userPinList ->
                        if (userPinList == null || userPinList.size() > 1) {
                            throw AppErrors.INSTANCE.userPinIncorrect().exception()
                        }

                        List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                        CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                            return hash.matches(userLoginAttempt.value, userPinList.get(0).pinHash)
                        }

                        userLoginAttempt.setSucceeded(matched != null)
                        return checkMaximumRetryCount(user, userLoginAttempt).then {
                            return checkMaximumSameUserAttemptCount(user, userLoginAttempt)
                        }
                    }
                }
            }
        }
    }

    private Promise<User> findUser(UserCredentialVerifyAttempt userLoginAttempt) {
        if (isEmail(userLoginAttempt.username)) {
            return userPersonalInfoRepository.searchByEmail(userLoginAttempt.username.toLowerCase(Locale.ENGLISH), null, Integer.MAX_VALUE,
                    0).then { List<UserPersonalInfo> personalInfos ->
                    if (CollectionUtils.isEmpty(personalInfos)) {
                        throw AppErrors.INSTANCE.userNotFoundByName(userLoginAttempt.username).exception()
                    }

                    return getDefaultEmailUser(personalInfos).then { User user ->
                        if (user == null) {
                            throw AppErrors.INSTANCE.userNotFoundByName(userLoginAttempt.username).exception()
                        }

                        if (user.isAnonymous || user.status != UserStatus.ACTIVE.toString()) {
                            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'User in invalid status.').
                                    exception()
                        }

                        if (CollectionUtils.isEmpty(user.emails)) {
                            throw AppErrors.INSTANCE.userNotFoundByName(userLoginAttempt.username).exception()
                        }

                        return Promise.pure(user)
                    }
                }
        } else {
            return userRepository.searchUserByCanonicalUsername(normalizeService.normalize(userLoginAttempt.username))
        }
    }

    Promise<User> getDefaultEmailUser(List<UserPersonalInfo> userPersonalInfoList) {
        User user = null
        return Promise.each(userPersonalInfoList){ UserPersonalInfo info ->
            return userRepository.get(info.userId).then { User existing ->
                if (existing == null || CollectionUtils.isEmpty(existing.emails)) {
                    return Promise.pure(null)
                }

                UserPersonalInfoLink existingLink = existing.emails.find { UserPersonalInfoLink link ->
                    return link.isDefault && link.value == info.id
                }

                if (existingLink != null) {
                    user = existing
                    return Promise.pure(Promise.BREAK)
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(user)
        }
    }

    private Promise<Void> checkMaximumRetryCount(User user, UserCredentialVerifyAttempt userLoginAttempt) {

        return getActiveCredentialCreatedTime(user, userLoginAttempt).then { Date passwordActiveTime ->
            Long timeInterval = passwordActiveTime.getTime()

            return userLoginAttemptRepository.searchByUserIdAndCredentialTypeAndInterval(user.getId(), userLoginAttempt.type, timeInterval, maxRetryCount, 0).then {
                    List<UserCredentialVerifyAttempt> attemptList ->
                if (CollectionUtils.isEmpty(attemptList) || attemptList.size() < maxRetryCount) {
                    return Promise.pure(null)
                }

                UserCredentialVerifyAttempt successAttempt = attemptList.find { UserCredentialVerifyAttempt attempt ->
                    return attempt.succeeded
                }
                if (successAttempt != null) {
                    return Promise.pure(null)
                }

                throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'User reaches maximum allowed retry count').exception()
            }
        }
    }

    private Promise<Date> getActiveCredentialCreatedTime(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        if (userLoginAttempt.type == CredentialType.PASSWORD.toString()) {
            return userPasswordRepository.searchByUserIdAndActiveStatus(user.getId(), true, 1, 0).then { List<UserPassword> userPasswordList ->
                if (CollectionUtils.isEmpty(userPasswordList)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'No Active password exists').exception()
                }

                return Promise.pure(userPasswordList.get(0).createdTime)
            }
        } else {
            return userPinRepository.searchByUserIdAndActiveStatus(user.getId(), true, 1, 0).then { List<UserPin> userPinList ->
                if (CollectionUtils.isEmpty(userPinList)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'No Active pin exists').exception()
                }

                return Promise.pure(userPinList.get(0).createdTime)
            }
        }
    }

    private Promise<Void> checkMaximumSameUserAttemptCount(User user, UserCredentialVerifyAttempt userLoginAttempt) {
        Long timeInterval = System.currentTimeMillis() - sameUserAttemptRetryInterval * 1000
        return userLoginAttemptRepository.searchByUserIdAndCredentialTypeAndInterval(user.getId(), userLoginAttempt.type, timeInterval,
                maxSameUserAttemptCount + 1, 0).then { List<UserCredentialVerifyAttempt> attemptList ->
            if (CollectionUtils.isEmpty(attemptList) || attemptList.size() <= maxSameUserAttemptCount) {
                return Promise.pure(null)
            }
            throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'User reaches maximum login attempt').exception()
        }
    }

    private Promise<Void> checkMaximumSameIPAttemptCount(UserCredentialVerifyAttempt userLoginAttempt) {
        if (StringUtils.isEmpty(userLoginAttempt.ipAddress)) {
            return Promise.pure(null)
        }

        Long fromTimeStamp = System.currentTimeMillis() - sameIPRetryInterval * 1000

        return userLoginAttemptRepository.searchByIPAddressAndCredentialTypeAndInterval(userLoginAttempt.ipAddress, userLoginAttempt.type, fromTimeStamp,
                maxSameIPRetryCount + 1, 0).then { List<UserCredentialVerifyAttempt> attemptList ->
            if (CollectionUtils.isEmpty(attemptList) || attemptList.size() <= maxSameIPRetryCount) {
                return Promise.pure(null)
            }

            throw AppCommonErrors.INSTANCE.fieldInvalid('username', 'User reaches maximum login attempt').exception()
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
                throw AppCommonErrors.INSTANCE.fieldInvalid('ipAddress').exception()
            }
        }

        if (userLoginAttempt.type == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        }

        List<String> allowedTypes = CredentialType.values().collect { CredentialType credentialType ->
            credentialType.toString()
        }

        if (!(userLoginAttempt.type in allowedTypes)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('type', allowedTypes.join(',')).exception()
        }

        if (userLoginAttempt.userAgent != null) {
            if (userLoginAttempt.userAgent.length() > userAgentMaxLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('userAgent', userAgentMaxLength).exception()
            }

            if (userLoginAttempt.userAgent.length() < userAgentMinLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('userAgent', userAgentMinLength).exception()
            }
        }

        if (userLoginAttempt.value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value').exception()
        }
    }

    static boolean isEmail(String value) {
        if (value.contains(MAIL_IDENTIFIER)) {
            return true
        }
        return false
    }

    static Date getTimeFrame(Integer timeFrame) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -timeFrame)
        return calendar.time
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
    void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval
    }

    @Required
    void setMaxAttemptCount(Integer maxAttemptCount) {
        this.maxSameUserAttemptCount = maxAttemptCount
    }

    @Required
    void setAttemptRetryInterval(Integer attemptRetryInterval) {
        this.sameUserAttemptRetryInterval = attemptRetryInterval
    }

    @Required
    void setMaxSameIPRetryCount(Integer maxSameIPRetryCount) {
        this.maxSameIPRetryCount = maxSameIPRetryCount
    }

    @Required
    void setSameIPRetryInterval(Integer sameIPRetryInterval) {
        this.sameIPRetryInterval = sameIPRetryInterval
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
