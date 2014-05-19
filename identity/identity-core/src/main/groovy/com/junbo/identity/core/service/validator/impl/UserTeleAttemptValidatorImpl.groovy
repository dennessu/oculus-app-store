package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleAttemptId
import com.junbo.identity.core.service.validator.UserTeleAttemptValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTeleAttemptRepository
import com.junbo.identity.data.repository.UserTeleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions
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
class UserTeleAttemptValidatorImpl implements UserTeleAttemptValidator {

    // todo:    Need to refactor user tele attempt validation according to marshall's requirement.

    private UserRepository userRepository
    private UserTeleRepository userTeleRepository
    private UserTeleAttemptRepository userTeleAttemptRepository

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
    Promise<UserTeleAttempt> validateForGet(UserId userId, UserTeleAttemptId attemptId) {
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

            if (existingUser.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTeleAttemptRepository.get(attemptId).then { UserTeleAttempt userTeleAttempt ->
                if (userTeleAttempt == null) {
                    throw AppErrors.INSTANCE.userTeleAttemptNotFound(attemptId).exception()
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
    Promise<Void> validateForSearch(UserId userId, UserTeleAttemptListOptions options) {
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
    Promise<Void> validateForCreate(UserId userId, UserTeleAttempt attempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (attempt == null) {
            throw new IllegalArgumentException('attmpt is null')
        }

        if (attempt.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (attempt.userTeleId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userTeleId').exception()
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

        /*
        if (attempt.clientId != null) {
            if (attempt.clientId.length() > maxClientIdLength) {
                throw AppErrors.INSTANCE.fieldTooLong('clientId', maxClientIdLength).exception()
            }
            if (attempt.clientId.length() < minClientIdLength) {
                throw AppErrors.INSTANCE.fieldTooShort('clientId', minClientIdLength).exception()
            }
        }
        */

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }
            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }
            if (user.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTeleRepository.get(attempt.userTeleId).then { UserTeleCode userTeleCode ->
                if (userTeleCode == null) {
                    throw AppErrors.INSTANCE.userTeleCodeNotFound(attempt.userTeleId).exception()
                }

                if (userTeleCode.userId != userId) {
                    throw AppErrors.INSTANCE.fieldInvalidException('userId',
                            'userId isn\'t matched to userTeleId.').exception()
                }

                if (userTeleCode.active != true) {
                    throw AppErrors.INSTANCE.fieldInvalidException('userTeleId', 'Tele code isn\'t active.').exception()
                }

                if (userTeleCode.expiresBy.before(new Date())) {
                    throw AppErrors.INSTANCE.fieldInvalidException('userTeleId', 'Tele code expired.').exception()
                }

                if (userTeleCode.verifyCode == attempt.verifyCode) {
                    attempt.succeeded = true
                } else {
                    attempt.succeeded = false
                }

                attempt.userId = userId

                return checkMaximumRetryCount(user, attempt)
            }
        }
    }

    private Promise<Void> checkMaximumRetryCount(User user, UserTeleAttempt attempt) {
        if (attempt.succeeded == true) {
            return Promise.pure(null)
        }

        // todo:    Is it possible to add limitation and paging to control the size?
        // todo:    How to sort by some column?
        return userTeleAttemptRepository.search(new UserTeleAttemptListOptions(
                userId: (UserId)user.id,
                userTeleId: attempt.userTeleId
        )).then { List<UserTeleAttempt> userTeleAttemptList ->
            if (CollectionUtils.isEmpty(userTeleAttemptList)
                    || userTeleAttemptList.size() < maxTeleCodeAttemptNumber) {
                return Promise.pure(null)
            }

            userTeleAttemptList.sort(new Comparator<UserTeleAttempt>() {
                @Override
                int compare(UserTeleAttempt o1, UserTeleAttempt o2) {
                    return o2.createdTime <=> o1.createdTime
                }
            }
            )

            int size = 0;
            for (; size < maxTeleCodeAttemptNumber; size++) {
                if (userTeleAttemptList.get(size).succeeded == true) {
                    break
                }
            }

            if (size == maxTeleCodeAttemptNumber) {
                user.status = UserStatus.SUSPEND.toString()
                return createInNewTran(user).then {
                    throw AppErrors.INSTANCE.fieldInvalid('userTeleId',
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
    void setUserTeleRepository(UserTeleRepository userTeleRepository) {
        this.userTeleRepository = userTeleRepository
    }

    @Required
    void setUserTeleAttemptRepository(UserTeleAttemptRepository userTeleAttemptRepository) {
        this.userTeleAttemptRepository = userTeleAttemptRepository
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
