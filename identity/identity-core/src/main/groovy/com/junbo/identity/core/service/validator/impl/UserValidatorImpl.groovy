package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.*
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by kg on 3/17/14.
 */
@Component
@CompileStatic
class UserValidatorImpl implements UserValidator {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private UsernameValidator usernameValidator

    @Autowired
    private NameValidator nameValidator

    @Autowired
    private LocaleValidator localeValidator

    @Autowired
    private TimezoneValidator timezoneValidator

    @Autowired
    private BirthdayValidator birthdayValidator

    @Autowired
    private NickNameValidator nickNameValidator

    @Autowired
    private DisplayNameValidator displayNameValidator

    @Override
    Promise<Void> validateForCreate(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        if (user.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (user.active == null) {
            user.active = true
        } else if (!user.active) {
            throw AppErrors.INSTANCE.fieldInvalid('active', 'true').exception()
        }

        validateUserInfo(user)

        if (user.username != null) {
            usernameValidator.validateUsername(user.username)

            user.canonicalUsername = usernameValidator.normalizeUsername(user.username)

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

        if (user.name != null) {
            nameValidator.validateName(user.name)
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

        if (user.timezone != null) {
            if (!timezoneValidator.isValidTimezone(user.timezone)) {
                throw AppErrors.INSTANCE.fieldInvalid('timezone').exception()
            }
        } else {
            user.timezone = timezoneValidator.defaultTimezone
        }

        if (user.birthday != null) {
            if (!birthdayValidator.isValidBirthday(user.birthday)) {
                throw AppErrors.INSTANCE.fieldInvalid('birthday').exception()
            }
        }

        if (user.gender != null) {
            if (!(user.gender in ['male', 'female'])) {
                throw AppErrors.INSTANCE.fieldInvalid('gender', 'male, female').exception()
            }
        }

        if (user.displayNameType != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('displayNameType').exception()
        }

        user.displayNameType = displayNameValidator.getDisplayNameType(user)

        if (user.nickName != null) {
            nickNameValidator.validateNickName(user.nickName)
        }

        if (user.canonicalUsername != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('canonicalUsername').exception()
        }
    }
}
