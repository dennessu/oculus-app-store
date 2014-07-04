package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.identity.core.service.validator.UserTFAAttemptValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTFAAttemptRepository
import com.junbo.identity.data.repository.UserTFARepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.identity.spec.v1.option.list.UserTFAAttemptListOptions
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
 * Created by liangfu on 5/4/14.
 */
@CompileStatic
class UserTFAAttemptValidatorImpl implements UserTFAAttemptValidator {
    private UserRepository userRepository
    private UserTFARepository userTFARepository
    private UserTFAAttemptRepository userTFAAttemptRepository

    private Integer minVerifyCodeLength
    private Integer maxVerifyCodeLength

    private List<Pattern> allowedIPPatterns

    private Integer maxUserAgentLength
    private Integer minUserAgentLength

    private Integer minClientIdLength
    private Integer maxClientIdLength

    private Integer maxTeleCodeAttemptNumber
    private PlatformTransactionManager transactionManager

    @Override
    Promise<UserTFAAttempt> validateForGet(UserId userId, UserTFAAttemptId attemptId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (attemptId == null) {
            throw new IllegalArgumentException('userTeleAttemptId is null')
        }

        return userRepository.get(userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (existingUser.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (existingUser.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTFAAttemptRepository.get(attemptId).then { UserTFAAttempt userTeleAttempt ->
                if (userTeleAttempt == null) {
                    throw AppErrors.INSTANCE.userTFAAttemptNotFound(attemptId).exception()
                }

                if (userTeleAttempt.userId != userId) {
                    throw AppErrors.INSTANCE.fieldInvalidException('userTeleAttemptId',
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
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (attempt.userTFAId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userTFAId').exception()
        }

        if (attempt.verifyCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('verifyCode').exception()
        }
        if (attempt.verifyCode.length() > maxVerifyCodeLength) {
            throw AppErrors.INSTANCE.fieldTooLong('verifyCode', maxVerifyCodeLength).exception()
        }
        if (attempt.verifyCode.length() < minVerifyCodeLength) {
            throw AppErrors.INSTANCE.fieldTooShort('verifyCode', minVerifyCodeLength).exception()
        }

        if (attempt.ipAddress != null) {
            if (!allowedIPPatterns.any {
                Pattern pattern -> pattern.matcher(attempt.ipAddress).matches()
            }) {
                throw AppErrors.INSTANCE.fieldInvalid('ipAddress').exception()
            }
        }

        if (attempt.userId != null && attempt.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userId.toString()).exception()
        }

        if (attempt.userAgent != null) {
            if (attempt.userAgent.length() > maxUserAgentLength) {
                throw AppErrors.INSTANCE.fieldTooLong('userAgent', maxUserAgentLength).exception()
            }
            if (attempt.userAgent.length() < minUserAgentLength) {
                throw AppErrors.INSTANCE.fieldTooShort('userAgent', minUserAgentLength).exception()
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

            return userTFARepository.get(attempt.userTFAId).then { UserTFA userTFA ->
                if (userTFA == null) {
                    throw AppErrors.INSTANCE.userTFANotFound(attempt.userTFAId).exception()
                }

                if (userTFA.userId != userId) {
                    throw AppErrors.INSTANCE.fieldInvalidException('userId',
                            'userId isn\'t matched to userTFAId.').exception()
                }

                if (!userTFA.active) {
                    throw AppErrors.INSTANCE.fieldInvalidException('userTFAId', 'Tele code isn\'t active.').exception()
                }

                if (userTFA.expiresBy.before(new Date())) {
                    throw AppErrors.INSTANCE.fieldInvalidException('userTFAId', 'Tele code expired.').exception()
                }

                if (userTFA.verifyCode == attempt.verifyCode) {
                    attempt.succeeded = true
                } else {
                    attempt.succeeded = false
                }

                attempt.userId = userId

                return checkMaximumRetryCount(user, attempt)
            }
        }
    }

    private Promise<Void> checkMaximumRetryCount(User user, UserTFAAttempt attempt) {
        if (attempt.succeeded) {
            return Promise.pure(null)
        }

        return userTFAAttemptRepository.searchByUserIdAndUserTFAId((UserId)user.id, attempt.userTFAId,
                Integer.MAX_VALUE, 0).then { List<UserTFAAttempt> userTeleAttemptList ->
            if (CollectionUtils.isEmpty(userTeleAttemptList)
                    || userTeleAttemptList.size() < maxTeleCodeAttemptNumber) {
                return Promise.pure(null)
            }

            userTeleAttemptList.sort(new Comparator<UserTFAAttempt>() {
                @Override
                int compare(UserTFAAttempt o1, UserTFAAttempt o2) {
                    return o2.createdTime <=> o1.createdTime
                }
            }
            )

            int size = 0;
            for (; size < maxTeleCodeAttemptNumber; size++) {
                if (userTeleAttemptList.get(size).succeeded) {
                    break
                }
            }

            if (size == maxTeleCodeAttemptNumber) {
                return createInNewTran(user).then {
                    throw AppErrors.INSTANCE.fieldInvalid('userTFAId',
                            'UserTele attempt reaches the maximum.').exception()
                }
            }

            return Promise.pure(null)
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

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserTFARepository(UserTFARepository userTFARepository) {
        this.userTFARepository = userTFARepository
    }

    @Required
    void setUserTFAAttemptRepository(UserTFAAttemptRepository userTFAAttemptRepository) {
        this.userTFAAttemptRepository = userTFAAttemptRepository
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

    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }
}
