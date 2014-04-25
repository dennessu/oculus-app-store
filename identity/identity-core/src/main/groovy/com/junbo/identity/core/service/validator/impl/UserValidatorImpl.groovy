package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.*
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class UserValidatorImpl implements UserValidator {

    private UserRepository userRepository

    private UsernameValidator usernameValidator

    private LocaleRepository localeRepository

    private UserPersonalInfoRepository userPersonalInfoRepository

    private TimezoneValidator timezoneValidator

    private NormalizeService normalizeService

    private List<String> allowedTypes

    private Integer minLabelLength
    private Integer maxLabelLength

    @Override
    Promise<Void> validateForCreate(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        if (user.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (user.status == null) {
            user.status = UserStatus.ACTIVE.toString()
        }
        return validateUserInfo(user).then {
            if (user.username != null) {
                usernameValidator.validateUsername(user.username)

                user.canonicalUsername = normalizeService.normalize(user.username)

                return userRepository.getUserByCanonicalUsername(user.canonicalUsername).then { User existingUser ->
                    if (existingUser != null) {
                        throw AppErrors.INSTANCE.fieldDuplicate('username').exception()
                    }

                    return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(User user, User oldUser) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        if (oldUser == null) {
            throw new IllegalArgumentException('oldUser is null')
        }

        if (user.id != oldUser.id) {
            throw new IllegalArgumentException('user.id mismatch')
        }

        if (user.status == null) {
            throw AppErrors.INSTANCE.fieldRequired('active').exception()
        }

        if (user.isAnonymous == null) {
            throw AppErrors.INSTANCE.fieldRequired('isAnonymous').exception()
        }

        return validateUserInfo(user).then {
            if (user.username != oldUser.username) {
                if (user.canonicalUsername != oldUser.canonicalUsername) {
                    return userRepository.getUserByCanonicalUsername(user.canonicalUsername).then { User existingUser ->
                        if (existingUser != null) {
                            throw AppErrors.INSTANCE.fieldDuplicate('username').exception()
                        }

                        return Promise.pure(null)
                    }
                }
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<User> validateForGet(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId')
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }
            return Promise.pure(user)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserGetOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.username == null) {
            throw AppErrors.INSTANCE.parameterRequired('username').exception()
        }

        return Promise.pure(null)
    }

    private Promise<Void> validateUserInfo(User user) {
        if (user.username != null) {
            if (user.isAnonymous == true) {
                throw AppErrors.INSTANCE.fieldInvalid('isAnonymous', false.toString()).exception()
            }
            usernameValidator.validateUsername(user.username)
        } else {
            if (user.isAnonymous == false) {
                throw AppErrors.INSTANCE.fieldInvalid('isAnonymous', true.toString()).exception()
            }
        }

        if (user.preferredTimezone != null) {
            if (!timezoneValidator.isValidTimezone(user.preferredTimezone)) {
                throw AppErrors.INSTANCE.fieldInvalid('preferredTimezone').exception()
            }
        } else {
            user.preferredTimezone = timezoneValidator.defaultTimezone
        }

        if (user.isAnonymous == null) {
            throw AppErrors.INSTANCE.fieldInvalid('isAnonymous').exception()
        }

        if (user.status != null) {
            UserStatus matched = UserStatus.values().find { UserStatus userStatus ->
                return userStatus.toString() == userStatus
            }

            if (matched == null) {
                throw AppErrors.INSTANCE.fieldInvalid('status').exception()
            }
        }

        return validateLocale(user).then {
            UserPersonalInfoLink email = user.addressBook.find { UserPersonalInfoLink userPersonalInfoLink ->
                return userPersonalInfoLink.type != 'EMAIL'
            }

            if (email == null) {
                throw AppErrors.INSTANCE.fieldInvalid('addressBook').exception()
            }

            return validateUserPersonalInfoLink(user.addressBook.iterator()).then {
                return validateUserPersonalInfoLink(user.personalInfo.iterator()).then {
                    return Promise.pure(null)
                }
            }
        }
    }

    Promise<Void> validateUserPersonalInfoLink(Iterator<UserPersonalInfoLink> it) {
        if (it.hasNext()) {
            UserPersonalInfoLink userPersonalInfoLink = it.next();

            if (userPersonalInfoLink.label == null) {
                throw AppErrors.INSTANCE.fieldRequired('label').exception()
            }
            if (userPersonalInfoLink.label.length() > maxLabelLength) {
                throw AppErrors.INSTANCE.fieldTooLong('label', maxLabelLength).exception()
            }
            if (userPersonalInfoLink.label.length() < minLabelLength) {
                throw AppErrors.INSTANCE.fieldTooShort('label', minLabelLength).exception()
            }

            if (userPersonalInfoLink.type == null) {
                throw AppErrors.INSTANCE.fieldRequired('type').exception()
            }
            if (!(userPersonalInfoLink.type in allowedTypes)) {
                throw AppErrors.INSTANCE.fieldInvalid('type', allowedTypes.join(',')).exception()
            }

            if (userPersonalInfoLink.resourceLink == null) {
                throw AppErrors.INSTANCE.fieldRequired('resourceLink').exception()
            }
            userPersonalInfoRepository.get(userPersonalInfoLink.resourceLink).
                    then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoLink.resourceLink).exception()
                }

                return validateUserPersonalInfoLink(it)
            }
        }
        return Promise.pure(null)
    }

    Promise<Void> validateLocale(User user) {
        if (user.preferredLocale != null) {
            return localeRepository.get(user.preferredLocale).then { Locale locale ->
                if (locale == null) {
                    throw AppErrors.INSTANCE.fieldInvalid(user.preferredLocale.value).exception()
                }

                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUsernameValidator(UsernameValidator usernameValidator) {
        this.usernameValidator = usernameValidator
    }

    @Required
    void setTimezoneValidator(TimezoneValidator timezoneValidator) {
        this.timezoneValidator = timezoneValidator
    }

    @Required
    void setNormalizeService(NormalizeService normalizeService) {
        this.normalizeService = normalizeService
    }

    @Required
    void setLocaleRepository(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Required
    void setAllowedTypes(List<String> allowedTypes) {
        this.allowedTypes = allowedTypes
    }

    @Required
    void setMinLabelLength(Integer minLabelLength) {
        this.minLabelLength = minLabelLength
    }

    @Required
    void setMaxLabelLength(Integer maxLabelLength) {
        this.maxLabelLength = maxLabelLength
    }
}
