/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import com.junbo.identity.core.service.validator.UserSecurityQuestionAttemptValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
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
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class UserSecurityQuestionAttemptValidatorImpl implements UserSecurityQuestionAttemptValidator {

    private UserRepository userRepository
    private UserSecurityQuestionAttemptRepository attemptRepository
    private UserSecurityQuestionRepository userSecurityQuestionRepository

    private CredentialHashFactory credentialHashFactory

    private Integer valueMinLength
    private Integer valueMaxLength

    private List<Pattern> allowedIPPatterns

    private Integer userAgentMinLength
    private Integer userAgentMaxLength

    private Integer clientIdMinLength
    private Integer clientIdMaxLength

    private PlatformTransactionManager transactionManager
    private Integer maxRetryCount

    @Override
    Promise<UserSecurityQuestionVerifyAttempt> validateForGet(
            UserId userId, UserSecurityQuestionVerifyAttemptId attemptId) {
        if (attemptId == null) {
            throw new IllegalArgumentException('userSecurityQuestionAttemptId is null')
        }

        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return attemptRepository.get(attemptId).then { UserSecurityQuestionVerifyAttempt attempt ->
            if (attempt == null) {
                throw AppErrors.INSTANCE.userSecurityQuestionAttemptNotFound(attemptId).exception()
            }

            if (userId != attempt.userId) {
                throw AppErrors.INSTANCE.parameterInvalid('userId and attemptId doesn\'t match').exception()
            }

            return Promise.pure(attempt)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserSecurityQuestionAttemptListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserSecurityQuestionVerifyAttempt attempt) {
        if (attempt == null) {
            throw new IllegalArgumentException('userSecurityQuationAttempt is null')
        }

        if (attempt.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return checkBasicUserSecurityQuestionAttemptInfo(attempt).then {
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

                if (attempt.userId != null && attempt.userId != userId) {
                    throw AppErrors.INSTANCE.fieldInvalid('userId', attempt.userId.toString()).exception()
                }
                attempt.setUserId((UserId)user.id)

                return userSecurityQuestionRepository.get(attempt.userSecurityQuestionId).
                        then { UserSecurityQuestion userSecurityQuestion ->
                            if (userSecurityQuestion == null) {
                                throw AppErrors.INSTANCE.userSecurityQuestionNotFound().exception()
                            }

                            List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                            CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                                return hash.matches(attempt.value, userSecurityQuestion.answerHash)
                            }

                            attempt.setSucceeded(matched != null)

                            return checkMaximumRetryCount(attempt)
                        }
            }
        }
    }

    private Promise<Void> checkMaximumRetryCount(UserSecurityQuestionVerifyAttempt attempt) {
        return attemptRepository.searchByUserIdAndSecurityQuestionId(attempt.userId, attempt.userSecurityQuestionId,
                maxRetryCount, 0).then { List<UserSecurityQuestionVerifyAttempt> attemptList ->
            if (CollectionUtils.isEmpty(attemptList) || attemptList.size() < maxRetryCount) {
                return Promise.pure(null)
            }

            int index = 0
            for (; index < maxRetryCount; index++) {
                if (attemptList.get(index).succeeded) {
                    break
                }
            }

            // If it reaches maxRetryCount, any retry will be treated as false login
            if (index == maxRetryCount) {
                throw AppErrors.INSTANCE.fieldInvalid('userId',
                        'User reaches maximum allowed retry count').exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> checkBasicUserSecurityQuestionAttemptInfo(UserSecurityQuestionVerifyAttempt attempt) {
        if (attempt == null) {
            throw new IllegalArgumentException('userSecurityQuestionAttempt is null')
        }

        if (attempt.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
        if (attempt.value.size() > valueMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', valueMaxLength).exception()
        }
        if (attempt.value.size() < valueMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', valueMinLength).exception()
        }

        if (attempt.ipAddress != null) {
            if (!allowedIPPatterns.any {
                Pattern pattern -> pattern.matcher(attempt.ipAddress).matches()
            }) {
                throw AppErrors.INSTANCE.fieldInvalid('ipAddress').exception()
            }
        }

        if (attempt.userAgent != null) {
            if (attempt.userAgent.size() > userAgentMaxLength) {
                throw AppErrors.INSTANCE.fieldTooLong('userAgent', userAgentMaxLength).exception()
            }
            if (attempt.userAgent.size() < userAgentMinLength) {
                throw AppErrors.INSTANCE.fieldTooShort('userAgent', userAgentMinLength).exception()
            }
        }

        if (attempt.userSecurityQuestionId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userSecurityQuestionId').exception()
        }

        return userSecurityQuestionRepository.get(attempt.userSecurityQuestionId).then {
            UserSecurityQuestion userSecurityQuestion ->
                if (userSecurityQuestion == null) {
                    throw AppErrors.INSTANCE.userSecurityQuestionNotFound(attempt.userSecurityQuestionId).exception()
                }

                return Promise.pure(null)
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setAttemptRepository(UserSecurityQuestionAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository
    }

    @Required
    void setUserSecurityQuestionRepository(UserSecurityQuestionRepository userSecurityQuestionRepository) {
        this.userSecurityQuestionRepository = userSecurityQuestionRepository
    }

    @Required
    void setCredentialHashFactory(CredentialHashFactory credentialHashFactory) {
        this.credentialHashFactory = credentialHashFactory
    }

    @Required
    void setValueMinLength(Integer valueMinLength) {
        this.valueMinLength = valueMinLength
    }

    @Required
    void setValueMaxLength(Integer valueMaxLength) {
        this.valueMaxLength = valueMaxLength
    }

    @Required
    void setAllowedIPPatterns(List<String> allowedIPPatterns) {
        this.allowedIPPatterns = allowedIPPatterns.collect {
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
    void setClientIdMinLength(Integer clientIdMinLength) {
        this.clientIdMinLength = clientIdMinLength
    }

    @Required
    void setClientIdMaxLength(Integer clientIdMaxLength) {
        this.clientIdMaxLength = clientIdMaxLength
    }

    @Required
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }

    @Required
    void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount
    }
}
