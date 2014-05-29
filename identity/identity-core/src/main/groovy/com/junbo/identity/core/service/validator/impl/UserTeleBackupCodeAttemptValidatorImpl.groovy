package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.identity.core.service.validator.UserTeleBackupCodeAttemptValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTeleBackupCodeAttemptRepository
import com.junbo.identity.data.repository.UserTeleBackupCodeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
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
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
class UserTeleBackupCodeAttemptValidatorImpl implements UserTeleBackupCodeAttemptValidator {

    private UserRepository userRepository
    private UserTeleBackupCodeRepository userTeleBackupCodeRepository
    private UserTeleBackupCodeAttemptRepository userTeleBackupCodeAttemptRepository

    private Integer minVerifyCodeLength
    private Integer maxVerifyCodeLength

    private List<Pattern> allowedIPPatterns

    private Integer maxUserAgentLength
    private Integer minUserAgentLength

    private Integer minClientIdLength
    private Integer maxClientIdLength

    private Integer maxRetryCount
    private PlatformTransactionManager transactionManager

    @Override
    Promise<UserTeleBackupCodeAttempt> validateForGet(UserId userId, UserTeleBackupCodeAttemptId attemptId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (attemptId == null) {
            throw new IllegalArgumentException('userTeleBackupCodeAttemptId is null')
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

            return userTeleBackupCodeAttemptRepository.get(attemptId).then { UserTeleBackupCodeAttempt attempt ->
                if (attempt == null) {
                    throw AppErrors.INSTANCE.userTeleBackupCodeAttemptNotFound(attemptId).exception()
                }

                if (attempt.userId != userId) {
                    throw AppErrors.INSTANCE.fieldInvalid('attemptId', 'AttemptId and userId don\'t match.').exception()
                }

                return Promise.pure(attempt)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserId userId, UserTeleBackupCodeAttemptListOptions options) {
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
    Promise<Void> validateForCreate(UserId userId, UserTeleBackupCodeAttempt attempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        if (attempt == null) {
            throw new IllegalArgumentException('attempt is null')
        }

        if (attempt.verifyCode == null) {
            throw AppErrors.INSTANCE.fieldInvalid('verifyCode').exception()
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

            attempt.userId = userId

            return userTeleBackupCodeRepository.search(new UserTeleBackupCodeListOptions(
                    userId: userId,
                    active: true
            )).then { List<UserTeleBackupCode> userTeleBackupCodeList ->
                if (CollectionUtils.isEmpty(userTeleBackupCodeList)) {
                    throw AppErrors.INSTANCE.userTeleBackupCodeIncorrect().exception()
                }

                if (userTeleBackupCodeList.any { UserTeleBackupCode userTeleBackupCode ->
                    return (userTeleBackupCode.active && userTeleBackupCode.expiresBy.after(new Date())
                        && userTeleBackupCode.verifyCode == attempt.verifyCode)
                }) {
                    attempt.succeeded = true
                } else {
                    attempt.succeeded = false
                }

                return checkMaximumRetryCount(user, attempt)
            }
        }
    }

    private Promise<Void> checkMaximumRetryCount(User user, UserTeleBackupCodeAttempt attempt) {
        if (attempt.succeeded) {
            return Promise.pure(null)
        }

        return userTeleBackupCodeAttemptRepository.searchByUserId((UserId)user.id, Integer.MAX_VALUE,
                0).then { List<UserTeleBackupCodeAttempt> attemptList ->
            if (CollectionUtils.isEmpty(attemptList) || attemptList.size() < maxRetryCount) {
                return Promise.pure(null)
            }

            attemptList.sort(new Comparator<UserTeleBackupCodeAttempt>() {
                @Override
                int compare(UserTeleBackupCodeAttempt o1, UserTeleBackupCodeAttempt o2) {
                    return o2.createdTime <=> o1.createdTime
                }
            })

            int index = 0
            for ( ; index < maxRetryCount; index++) {
                if (attemptList.get(index).succeeded) {
                    break
                }
            }

            if (index == maxRetryCount) {
                user.status = UserStatus.SUSPEND.toString()
                return createInNewTran(user).then {
                    throw AppErrors.INSTANCE.fieldInvalid('verifyCode', 'Attempt reaches maximum.').exception()
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
    void setUserTeleBackupCodeRepository(UserTeleBackupCodeRepository userTeleBackupCodeRepository) {
        this.userTeleBackupCodeRepository = userTeleBackupCodeRepository
    }

    @Required
    void setUserTeleBackupCodeAttemptRepository(UserTeleBackupCodeAttemptRepository userTeleBackupCodeAttemptRepository) {
        this.userTeleBackupCodeAttemptRepository = userTeleBackupCodeAttemptRepository
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
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }
}

