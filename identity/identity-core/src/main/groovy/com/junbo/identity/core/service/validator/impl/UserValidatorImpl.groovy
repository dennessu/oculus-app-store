package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.*
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
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

    private LocaleValidator localeValidator

    private CurrencyValidator currencyValidator

    private TimezoneValidator timezoneValidator

    private NickNameValidator nickNameValidator

    private NormalizeService normalizeService

    @Override
    Promise<Void> validateForCreate(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        if (user.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        validateUserInfo(user)

        if (user.active == null) {
            user.active = true
        } else if (!user.active) {
            throw AppErrors.INSTANCE.fieldInvalid('active', 'true').exception()
        }

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

        if (user.active == null) {
            throw AppErrors.INSTANCE.fieldRequired('active').exception()
        }

        validateUserInfo(user)

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

    private void validateUserInfo(User user) {
        if (!(user.type in ['user', 'anonymousUser'])) {
            throw AppErrors.INSTANCE.fieldInvalid('type', 'user, anonymousUser').exception()
        }

        if (user.preferredLanguage != null) {
            if (!localeValidator.isValidLocale(user.preferredLanguage)) {
                throw AppErrors.INSTANCE.fieldInvalid('preferredLanguage').exception()
            }
        } else {
            user.preferredLanguage = localeValidator.defaultLocale
        }

        if (user.locale != null) {
            if (!localeValidator.isValidLocale(user.locale)) {
                throw AppErrors.INSTANCE.fieldInvalid('locale').exception()
            }
        } else {
            user.locale = localeValidator.defaultLocale
        }

        if (user.currency != null) {
            if (!currencyValidator.isValid(user.currency)) {
                throw AppErrors.INSTANCE.fieldInvalid('currency').exception()
            }
        } else {
            user.currency = currencyValidator.default
        }

        if (user.timezone != null) {
            if (!timezoneValidator.isValidTimezone(user.timezone)) {
                throw AppErrors.INSTANCE.fieldInvalid('timezone').exception()
            }
        } else {
            user.timezone = timezoneValidator.defaultTimezone
        }

        if (user.nickName != null) {
            nickNameValidator.validateNickName(user.nickName)
        }
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
    void setLocaleValidator(LocaleValidator localeValidator) {
        this.localeValidator = localeValidator
    }

    @Required
    void setCurrencyValidator(CurrencyValidator currencyValidator) {
        this.currencyValidator = currencyValidator
    }

    @Required
    void setTimezoneValidator(TimezoneValidator timezoneValidator) {
        this.timezoneValidator = timezoneValidator
    }

    @Required
    void setNickNameValidator(NickNameValidator nickNameValidator) {
        this.nickNameValidator = nickNameValidator
    }

    @Required
    void setNormalizeService(NormalizeService normalizeService) {
        this.normalizeService = normalizeService
    }
}
