package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.common.id.UserTFAId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.validator.UserTFAAttemptValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.service.UserService
import com.junbo.identity.service.UserTFAAttemptService
import com.junbo.identity.service.UserTFAMailService
import com.junbo.identity.service.UserTFAPhoneService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.identity.spec.v1.option.list.UserTFAAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

import java.util.regex.Pattern

/**
 * Created by liangfu on 5/4/14.
 */
@CompileStatic
class UserTFAAttemptValidatorImpl implements UserTFAAttemptValidator {
    private UserService userService
    private UserTFAPhoneService userTFAPhoneService
    private UserTFAAttemptService userTFAAttemptService
    private UserTFAMailService userTFAMailService

    private Integer minVerifyCodeLength
    private Integer maxVerifyCodeLength

    private List<Pattern> allowedIPPatterns

    private Integer maxUserAgentLength
    private Integer minUserAgentLength

    private Integer minClientIdLength
    private Integer maxClientIdLength

    private Integer maxTeleCodeAttemptNumber

    @Override
    Promise<UserTFAAttempt> validateForGet(UserId userId, UserTFAAttemptId attemptId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (attemptId == null) {
            throw new IllegalArgumentException('userTeleAttemptId is null')
        }

        return userService.getNonDeletedUser(userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (existingUser.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (existingUser.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTFAAttemptService.get(attemptId).then { UserTFAAttempt userTeleAttempt ->
                if (userTeleAttempt == null) {
                    throw AppErrors.INSTANCE.userTFAAttemptNotFound(attemptId).exception()
                }

                if (userTeleAttempt.userId != userId) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('userTeleAttemptId',
                            'userId doesn\'t match with userTeleAttempt.').exception()
                }

                return Promise.pure(userTeleAttempt)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserId userId, UserTFAAttemptListOptions options) {
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
    Promise<Void> validateForCreate(UserId userId, UserTFAAttempt attempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (attempt == null) {
            throw new IllegalArgumentException('attmpt is null')
        }

        if (attempt.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }
        if (attempt.userTFAId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userTFAId').exception()
        }

        if (attempt.verifyCode == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('verifyCode').exception()
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

        return userService.getNonDeletedUser(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }
            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }
            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return getUserTFA(attempt.userTFAId).then { UserTFA userTFA ->
                if (userTFA == null) {
                    throw AppErrors.INSTANCE.userTFANotFound(attempt.userTFAId).exception()
                }

                if (userTFA.userId != userId) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('userId',
                            'userId isn\'t matched to userTFAId.').exception()
                }

                if (!userTFA.active) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('userTFAId', 'Tele code isn\'t active.').exception()
                }

                if (userTFA.expiresBy.before(new Date())) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('userTFAId', 'Tele code expired.').exception()
                }

                attempt.succeeded = (userTFA.verifyCode == attempt.verifyCode)
                attempt.userId = userId

                return checkMaximumRetryCount(user, attempt)
            }
        }
    }

    private Promise<UserTFA> getUserTFA(UserTFAId userTFAId) {
        return userTFAPhoneService.get(userTFAId).then { UserTFA userTFA ->
            if (userTFA == null) {
                return userTFAMailService.get(userTFAId)
            } else {
                return Promise.pure(userTFA)
            }
        }
    }

    private Promise<Void> checkMaximumRetryCount(User user, UserTFAAttempt attempt) {
         return userTFAAttemptService.searchByUserIdAndUserTFAId((UserId)user.id, attempt.userTFAId, maxTeleCodeAttemptNumber, 0).then { Results<UserTFAAttempt> userTeleAttemptList ->
            if (userTeleAttemptList == null || CollectionUtils.isEmpty(userTeleAttemptList.items) || userTeleAttemptList.items.size() < maxTeleCodeAttemptNumber) {
                return Promise.pure(null)
            }

            UserTFAAttempt userTFAAttempt = userTeleAttemptList.items.find { UserTFAAttempt tfaAttempt ->
                return tfaAttempt.succeeded
            }

            if (userTFAAttempt == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('userTFAId', 'UserTele attempt reaches the maximum.').exception()
            }

            return Promise.pure(null)
        }
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setUserTFAPhoneService(UserTFAPhoneService userTFAPhoneService) {
        this.userTFAPhoneService = userTFAPhoneService
    }

    @Required
    void setUserTFAAttemptService(UserTFAAttemptService userTFAAttemptService) {
        this.userTFAAttemptService = userTFAAttemptService
    }

    @Required
    void setUserTFAMailService(UserTFAMailService userTFAMailService) {
        this.userTFAMailService = userTFAMailService
    }

    @Required
    void setAllowedIPPatterns(List<String> allowedIPPatterns) {
        this.allowedIPPatterns = allowedIPPatterns.collect {
            String pattern -> Pattern.compile(pattern)
        }
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
    void setMaxTeleCodeAttemptNumber(Integer maxTeleCodeAttemptNumber) {
        this.maxTeleCodeAttemptNumber = maxTeleCodeAttemptNumber
    }
}
