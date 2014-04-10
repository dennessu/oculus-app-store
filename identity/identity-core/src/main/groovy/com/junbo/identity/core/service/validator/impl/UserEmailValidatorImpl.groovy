package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserEmailId
import com.junbo.common.id.UserId
import com.junbo.identity.core.service.validator.UserEmailValidator
import com.junbo.identity.data.repository.UserEmailRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserEmail
import com.junbo.identity.spec.options.list.UserEmailListOptions
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserEmailValidatorImpl implements UserEmailValidator {
    private UserEmailRepository userEmailRepository
    private UserRepository userRepository

    private List<String> allowedEmailTypes

    private List<Pattern> allowedEmailPatterns
    private Integer emailMinLength
    private Integer emailMaxLength

    @Override
    Promise<UserEmail> validateForGet(UserId userId, UserEmailId userEmailId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }
        if (userEmailId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userEmailId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            userEmailRepository.get(userEmailId).then { UserEmail userEmail ->
                if (userEmail == null) {
                    throw AppErrors.INSTANCE.userEmailNotFound(userEmailId).exception()
                }

                if (userId != userEmail.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userEmailId doesn\'t match').
                            exception()
                }

                return Promise.pure(userEmail)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserEmailListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null && options.value == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId or value').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserEmail userEmail) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        basicCheckUserEmailInfo(userEmail)
        if (userEmail.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (userEmail.userId != null && userEmail.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userEmail.userId.toString()).exception()
        }

        return userEmailRepository.search(new UserEmailListOptions(
                userId: userId,
                type: userEmail.type,
                value: userEmail.value
        )).then { List<UserEmail> existing ->
            if (existing != null && existing.size() != 0) {
                throw AppErrors.INSTANCE.fieldDuplicate('type & value').exception()
            }

            userEmail.setUserId(userId)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserEmailId userEmailId,
                                    UserEmail userEmail, UserEmail oldUserEmail) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        return validateForGet(userId, userEmailId).then {
            basicCheckUserEmailInfo(userEmail)

            if (userEmail.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userEmail.id != userEmailId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userEmailId.value.toString()).exception()
            }

            if (userEmail.id != oldUserEmail.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserEmail.id.toString()).exception()
            }

            if (userEmail.value != oldUserEmail.value || userEmail.type != oldUserEmail.type) {
                userEmailRepository.search(new UserEmailListOptions(
                        userId: userId,
                        type: userEmail.type,
                        value: userEmail.value
                )).then {
                    List<UserEmail> existing ->
                        if (existing != null && existing.size() != 0) {
                            throw AppErrors.INSTANCE.fieldDuplicate('type & value').exception()
                        }
                }
                userEmail.setUserId(userId)
                return Promise.pure(null)
            }

            return Promise.pure(null)
        }
    }

    private void basicCheckUserEmailInfo(UserEmail userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException('userEmail is null')
        }

        if (userEmail.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }
        if (!(userEmail.type in allowedEmailTypes)) {
            throw AppErrors.INSTANCE.fieldInvalid('type', allowedEmailTypes.join(',')).exception()
        }

        if (userEmail.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
        // todo:    Need to enable this
        /*
        if (!allowedEmailPatterns.any {
            Pattern pattern -> pattern.matcher(userEmail.value).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }
        */

        if (userEmail.primary == null) {
            throw AppErrors.INSTANCE.fieldRequired('primary').exception()
        }
        if (userEmail.verified == null) {
            throw AppErrors.INSTANCE.fieldRequired('verified').exception()
        }
    }

    @Required
    void setUserEmailRepository(UserEmailRepository userEmailRepository) {
        this.userEmailRepository = userEmailRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setAllowedEmailTypes(List<String> allowedEmailTypes) {
        this.allowedEmailTypes = allowedEmailTypes
    }

    @Required
    void setAllowedEmailPatterns(List<String> allowedEmailPatterns) {
        this.allowedEmailPatterns = allowedEmailPatterns.collect { String allowedEmailPattern ->
            Pattern.compile(allowedEmailPattern)
        }
    }

    @Required
    void setEmailMinLength(Integer emailMinLength) {
        this.emailMinLength = emailMinLength
    }

    @Required
    void setEmailMaxLength(Integer emailMaxLength) {
        this.emailMaxLength = emailMaxLength
    }
}
