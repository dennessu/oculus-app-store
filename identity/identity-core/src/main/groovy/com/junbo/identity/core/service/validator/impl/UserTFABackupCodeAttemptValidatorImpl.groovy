package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.core.service.validator.UserTFABackupCodeAttemptValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTFAPhoneBackupCodeAttemptRepository
import com.junbo.identity.data.repository.UserTFAPhoneBackupCodeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

import java.util.regex.Pattern

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
class UserTFABackupCodeAttemptValidatorImpl implements UserTFABackupCodeAttemptValidator {

    private UserRepository userRepository
    private UserTFAPhoneBackupCodeRepository userTFABackupCodeRepository
    private UserTFAPhoneBackupCodeAttemptRepository userTFABackupCodeAttemptRepository

    private Integer minVerifyCodeLength
    private Integer maxVerifyCodeLength

    private List<Pattern> allowedIPPatterns

    private Integer maxUserAgentLength
    private Integer minUserAgentLength

    private Integer minClientIdLength
    private Integer maxClientIdLength

    private Integer maxRetryCount

    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    @Override
    Promise<UserTFABackupCodeAttempt> validateForGet(UserId userId, UserTFABackupCodeAttemptId attemptId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (attemptId == null) {
            throw new IllegalArgumentException('userTFABackupCodeAttemptId is null')
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTFABackupCodeAttemptRepository.get(attemptId).then { UserTFABackupCodeAttempt attempt ->
                if (attempt == null) {
                    throw AppErrors.INSTANCE.userTFABackupCodeAttemptNotFound(attemptId).exception()
                }

                if (attempt.userId != userId) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('attemptId', 'AttemptId and userId don\'t match.').exception()
                }

                return Promise.pure(attempt)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserId userId, UserTFABackupCodeAttemptListOptions options) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }
        options.userId = userId

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTFABackupCodeAttempt attempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        if (attempt == null) {
            throw new IllegalArgumentException('attempt is null')
        }

        if (attempt.verifyCode == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('verifyCode').exception()
        }
        if (attempt.verifyCode.length() > maxVerifyCodeLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('verifyCode', maxVerifyCodeLength).exception()
        }
        if (attempt.verifyCode.length() < minVerifyCodeLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('verifyCode', minVerifyCodeLength).exception()
        }

        if (attempt.ipAddress != null) {
            if (!allowedIPPatterns.any {
                Pattern pattern -> pattern.matcher(attempt.ipAddress).matches()
            }) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('ipAddress').exception()
            }
        }

        if (attempt.userId != null && attempt.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', attempt.userId, userId).exception()
        }

        if (attempt.userAgent != null) {
            if (attempt.userAgent.length() > maxUserAgentLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('userAgent', maxUserAgentLength).exception()
            }
            if (attempt.userAgent.length() < minUserAgentLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('userAgent', minUserAgentLength).exception()
            }
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }
            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            attempt.userId = userId

            return userTFABackupCodeRepository.searchByUserIdAndActiveStatus(userId, true, maximumFetchSize,
                    0).then { List<UserTFABackupCode> userTFABackupCodeList ->
                if (CollectionUtils.isEmpty(userTFABackupCodeList)) {
                    throw AppErrors.INSTANCE.userTFABackupCodeIncorrect().exception()
                }

                if (userTFABackupCodeList.any { UserTFABackupCode userTFABackupCode ->
                    return (userTFABackupCode.active && userTFABackupCode.expiresBy.after(new Date())
                        && userTFABackupCode.verifyCode == attempt.verifyCode)
                }) {
                    attempt.succeeded = true
                } else {
                    attempt.succeeded = false
                }

                return checkMaximumRetryCount(user, attempt)
            }
        }
    }

    private Promise<Void> checkMaximumRetryCount(User user, UserTFABackupCodeAttempt attempt) {
        if (attempt.succeeded) {
            return Promise.pure(null)
        }

        return userTFABackupCodeAttemptRepository.searchByUserId((UserId)user.id, maxRetryCount, 0).then { List<UserTFABackupCodeAttempt> attemptList ->
            if (CollectionUtils.isEmpty(attemptList) || attemptList.size() < maxRetryCount) {
                return Promise.pure(null)
            }

            UserTFABackupCodeAttempt userTFABackupCodeAttempt = attemptList.find { UserTFABackupCodeAttempt backupCodeAttempt ->
                return backupCodeAttempt.succeeded
            }

            if (userTFABackupCodeAttempt == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('verifyCode', 'Attempt reaches maximum.').exception()
            }

            return Promise.pure(null)
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserTFABackupCodeRepository(UserTFAPhoneBackupCodeRepository userTFABackupCodeRepository) {
        this.userTFABackupCodeRepository = userTFABackupCodeRepository
    }

    @Required
    void setUserTFABackupCodeAttemptRepository(UserTFAPhoneBackupCodeAttemptRepository userTFABackupCodeAttemptRepository) {
        this.userTFABackupCodeAttemptRepository = userTFABackupCodeAttemptRepository
    }

    @Required
    void setMinVerifyCodeLength(Integer minVerifyCodeLength) {
        this.minVerifyCodeLength = minVerifyCodeLength
    }

    @Required
    void setMaxVerifyCodeLength(Integer maxVerifyCodeLength) {
        this.maxVerifyCodeLength = maxVerifyCodeLength
    }

    @Required
    void setAllowedIPPatterns(List<String> allowedIPPatterns) {
        this.allowedIPPatterns = allowedIPPatterns.collect {
            String pattern -> Pattern.compile(pattern)
        }
    }

    @Required
    void setMaxUserAgentLength(Integer maxUserAgentLength) {
        this.maxUserAgentLength = maxUserAgentLength
    }

    @Required
    void setMinUserAgentLength(Integer minUserAgentLength) {
        this.minUserAgentLength = minUserAgentLength
    }

    @Required
    void setMinClientIdLength(Integer minClientIdLength) {
        this.minClientIdLength = minClientIdLength
    }

    @Required
    void setMaxClientIdLength(Integer maxClientIdLength) {
        this.maxClientIdLength = maxClientIdLength
    }

    @Required
    void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount
    }

    @Required
    void setMaximumFetchSize(Integer maximumFetchSize) {
        this.maximumFetchSize = maximumFetchSize
    }
}

