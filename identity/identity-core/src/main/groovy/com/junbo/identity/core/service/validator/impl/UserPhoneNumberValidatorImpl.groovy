package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPhoneNumberId
import com.junbo.identity.core.service.validator.UserPhoneNumberValidator
import com.junbo.identity.data.repository.UserPhoneNumberRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPhoneNumber
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

import java.util.regex.Pattern

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPhoneNumberValidatorImpl implements UserPhoneNumberValidator {

    private UserPhoneNumberRepository userPhoneNumberRepository

    private UserRepository userRepository

    private Integer typeMinLength
    private Integer typeMaxLength

    private Integer valueMinLength
    private Integer valueMaxLength
    private List<Pattern> allowedValuePatterns

    @Override
    Promise<UserPhoneNumber> validateForGet(UserId userId, UserPhoneNumberId userPhoneNumberId) {

        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userPhoneNumberId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userPhoneNumberId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return userPhoneNumberRepository.get(userPhoneNumberId).then { UserPhoneNumber userPhoneNumber ->
                if (userPhoneNumber == null) {
                    throw AppErrors.INSTANCE.userPhoneNumberNotFound(userPhoneNumberId).exception()
                }

                if (userId != userPhoneNumber.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userPhoneNumberId doesn\'t match.').
                            exception()
                }

                return Promise.pure(userPhoneNumber)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserPhoneNumberListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserPhoneNumber userPhoneNumber) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        checkBasicUserPhoneNumberInfo(userPhoneNumber)
        if (userPhoneNumber.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userPhoneNumber.userId != null && userPhoneNumber.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userPhoneNumber.userId.toString()).exception()
        }

        return userPhoneNumberRepository.search(new UserPhoneNumberListOptions(
                userId: userId,
                type: userPhoneNumber.type,
                value: userPhoneNumber.value
        )).then { List<UserPhoneNumber> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('type&value').exception()
            }

            userPhoneNumber.setUserId(userId)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserPhoneNumberId userPhoneNumberId,
                                    UserPhoneNumber userPhoneNumber, UserPhoneNumber oldUserPhoneNumber) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return validateForGet(userId, userPhoneNumberId).then {
            checkBasicUserPhoneNumberInfo(userPhoneNumber)

            if (userPhoneNumber.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userPhoneNumber.id != userPhoneNumberId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userPhoneNumberId.value.toString()).exception()
            }

            if (userPhoneNumber.id != oldUserPhoneNumber.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserPhoneNumber.id.toString()).exception()
            }

            if (userPhoneNumber.type != oldUserPhoneNumber.type || userPhoneNumber.value != oldUserPhoneNumber.value) {
                return userPhoneNumberRepository.search(new UserPhoneNumberListOptions(
                        userId: userId,
                        type: userPhoneNumber.type,
                        value: userPhoneNumber.value
                )).then { List<UserPhoneNumber> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('type&value').exception()
                    }
                    userPhoneNumber.setUserId(userId)
                    return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

    void checkBasicUserPhoneNumberInfo(UserPhoneNumber userPhoneNumber) {
        if (userPhoneNumber == null) {
            throw new IllegalArgumentException('userPhoneNumber is null')
        }
        if (userPhoneNumber.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }
        if (userPhoneNumber.type.size() > typeMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('type', typeMaxLength).exception()
        }
        if (userPhoneNumber.type.size() < typeMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('type', typeMinLength).exception()
        }

        if (userPhoneNumber.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
        if (userPhoneNumber.value.size() > valueMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', valueMaxLength).exception()
        }
        if (userPhoneNumber.value.size() < valueMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', valueMinLength).exception()
        }

        if (!allowedValuePatterns.any {
            Pattern pattern -> pattern.matcher(userPhoneNumber.value).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }

        if (userPhoneNumber.primary == null) {
            throw AppErrors.INSTANCE.fieldRequired('primary').exception()
        }

        if (userPhoneNumber.verified == null) {
            throw AppErrors.INSTANCE.fieldRequired('verified').exception()
        }
    }

    @Required
    void setUserPhoneNumberRepository(UserPhoneNumberRepository userPhoneNumberRepository) {
        this.userPhoneNumberRepository = userPhoneNumberRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setTypeMinLength(Integer typeMinLength) {
        this.typeMinLength = typeMinLength
    }

    @Required
    void setTypeMaxLength(Integer typeMaxLength) {
        this.typeMaxLength = typeMaxLength
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
    void setAllowedValuePatterns(List<String> allowedValuePatterns) {
        this.allowedValuePatterns = allowedValuePatterns.collect {
            String allowedValuePattern -> Pattern.compile(allowedValuePattern)
        }
    }
}
