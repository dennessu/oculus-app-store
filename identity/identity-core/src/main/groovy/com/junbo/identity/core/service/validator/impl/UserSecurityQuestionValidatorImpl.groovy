package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.core.service.util.CipherHelper
import com.junbo.identity.core.service.validator.UserSecurityQuestionValidator
import com.junbo.identity.data.repository.SecurityQuestionRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.domaindata.SecurityQuestion
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserSecurityQuestionValidatorImpl implements UserSecurityQuestionValidator {

    private UserRepository userRepository
    private UserSecurityQuestionRepository userSecurityQuestionRepository
    private SecurityQuestionRepository securityQuestionRepository

    private Integer answerMinLength
    private Integer answerMaxLength

    @Override
    Promise<UserSecurityQuestion> validateForGet(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {

        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userSecurityQuestionId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userSecurityQuestionId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return userSecurityQuestionRepository.get(userSecurityQuestionId).
                    then { UserSecurityQuestion userSecurityQuestion ->
                if (userSecurityQuestion == null) {
                    throw AppErrors.INSTANCE.userSecurityQuestionNotFound(userSecurityQuestionId).exception()
                }

                if (userId != userSecurityQuestion.userId) {
                    throw AppErrors.INSTANCE.
                            parameterInvalid('userId and userSecurityQuestionId doesn\'t match.').exception()
                }

                return Promise.pure(userSecurityQuestion)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserSecurityQuestionListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        checkBasicUserSecurityQuestionInfo(userSecurityQuestion)
        if (userSecurityQuestion.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userSecurityQuestion.userId != null && userSecurityQuestion.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userSecurityQuestion.userId.toString()).exception()
        }

        // Check whether this security question is used before
        userSecurityQuestionRepository.search(new UserSecurityQuestionListOptions(userId: userId)).then {
            List<UserSecurityQuestion> userSecurityQuestionList ->
                if (!CollectionUtils.isEmpty(userSecurityQuestionList)) {
                    boolean exists = userSecurityQuestionList.any { UserSecurityQuestion existing ->
                        return (existing.securityQuestion == userSecurityQuestion.securityQuestion)
                    }
                    if (exists) {
                        throw AppErrors.INSTANCE.fieldInvalid('securityQuestion').exception()
                    }
                }
                userSecurityQuestion.setUserId(userId)
                userSecurityQuestion.setAnswerSalt(UUID.randomUUID().toString())
                userSecurityQuestion.setAnswerHash(
                        CipherHelper.hashPassword(userSecurityQuestion.answer, userSecurityQuestion.answerSalt))
                return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                        UserSecurityQuestion userSecurityQuestion, UserSecurityQuestion oldUserSecurityQuestion) {
        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }
        if (oldUserSecurityQuestion == null) {
            throw new IllegalArgumentException('oldUserSecurityQuestion is null')
        }

        if (userSecurityQuestion.id != userSecurityQuestionId) {
            throw AppErrors.INSTANCE.fieldInvalid('userSecurityQuestionId').exception()
        }

        if (userSecurityQuestion.id != oldUserSecurityQuestion.id) {
            throw AppErrors.INSTANCE.fieldInvalid('userSecurityQuestionId').exception()
        }

        if (userSecurityQuestion.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId').exception()
        }

        if (oldUserSecurityQuestion.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId').exception()
        }

        checkBasicUserSecurityQuestionInfo(userSecurityQuestion)

        validateForGet(userId, userSecurityQuestionId).then { UserSecurityQuestion existingSecurityQuestion ->
            if (existingSecurityQuestion == null) {
                throw AppErrors.INSTANCE.userSecurityQuestionNotFound(userSecurityQuestionId).exception()
            }
            if (userSecurityQuestion.userId != existingSecurityQuestion.userId) {
                throw AppErrors.INSTANCE.fieldInvalid('userId').exception()
            }

            if (userSecurityQuestion.securityQuestion != oldUserSecurityQuestion.securityQuestion) {
                userSecurityQuestionRepository.search(new UserSecurityQuestionListOptions(userId: userId)).then {
                    List<UserSecurityQuestion> userSecurityQuestionList ->
                    boolean securityQuestionExists = userSecurityQuestionList.any { UserSecurityQuestion existing ->
                        return (existing.securityQuestion == userSecurityQuestion.securityQuestion)
                    }
                    if (securityQuestionExists) {
                        throw AppErrors.INSTANCE.fieldInvalid('securityQuestion').exception()
                    }
                    return Promise.pure(null)
                }
            }
        }
    }

    private void checkBasicUserSecurityQuestionInfo(UserSecurityQuestion userSecurityQuestion) {
        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }

        if (userSecurityQuestion.answer == null) {
            throw AppErrors.INSTANCE.fieldRequired('answer').exception()
        }
        if (userSecurityQuestion.answer.size() > answerMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('answer', answerMaxLength).exception()
        }
        if (userSecurityQuestion.answer.size() < answerMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('answer', answerMinLength).exception()
        }

        if (userSecurityQuestion.securityQuestion == null) {
            throw AppErrors.INSTANCE.fieldRequired('securityQuestion').exception()
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setSecurityQuestionRepository(SecurityQuestionRepository securityQuestionRepository) {
        this.securityQuestionRepository = securityQuestionRepository
    }

    @Required
    void setUserSecurityQuestionRepository(UserSecurityQuestionRepository userSecurityQuestionRepository) {
        this.userSecurityQuestionRepository = userSecurityQuestionRepository
    }

    @Required
    void setAnswerMinLength(Integer answerMinLength) {
        this.answerMinLength = answerMinLength
    }

    @Required
    void setAnswerMaxLength(Integer answerMaxLength) {
        this.answerMaxLength = answerMaxLength
    }
}
