package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.*
import com.junbo.identity.data.identifiable.UserPersonalInfoType
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
            if (UserStatus.values().any() { UserStatus userStatus ->
                return userStatus.toString() == user.status
            } == false)
            {
                throw AppErrors.INSTANCE.fieldInvalid('status').exception()
            }
        }

        return validateLocale(user).then {
            return validateAddresses(user)
        }.then {
            return validateEmails(user)
        }.then {
            return validatePhones(user)
        }.then {
            return validateName(user)
        }.then {
            return validateDob(user)
        }.then {
            return validateSMS(user)
        }.then {
            return validateQQ(user)
        }.then {
            return validateWhatsApp(user)
        }.then {
            return validatePassport(user)
        }.then {
            return validateGovernmentId(user)
        }.then {
            return validateDriversLicense(user)
        }.then {
            return validateGender(user)
        }
    }

    Promise<Void> validateUserPersonalInfoLinkIterator(Iterator<UserPersonalInfoLink> it, String type) {
        if (it.hasNext()) {
            UserPersonalInfoLink userPersonalInfoLink = it.next();

            if (userPersonalInfoLink.value == null) {
                throw AppErrors.INSTANCE.fieldRequired('value').exception()
            }

            return userPersonalInfoRepository.get(userPersonalInfoLink.value).
                    then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null) {
                            throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoLink.value).exception()
                        }

                        if (type != null) {
                            if (userPersonalInfo.type != type) {
                                throw AppErrors.INSTANCE.fieldInvalid(userPersonalInfoLink.value.toString()).exception()
                            }
                        }

                        return validateUserPersonalInfoLinkIterator(it, type)
                    }
        }
        return Promise.pure(null)
    }

    Promise<UserPersonalInfo> validateUserPersonalInfoLink(UserPersonalInfoLink userPersonalInfoLink) {
        if (userPersonalInfoLink.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
        return userPersonalInfoRepository.get(userPersonalInfoLink.value).
                then { UserPersonalInfo userPersonalInfo ->
                    if (userPersonalInfo == null) {
                        throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoLink.value).exception()
                    }

                    return Promise.pure(userPersonalInfo)
                }
    }

    Promise<Void> validateUserPersonalInfoLink(UserPersonalInfoLink userPersonalInfoLink, String type) {
        if (userPersonalInfoLink.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
        return userPersonalInfoRepository.get(userPersonalInfoLink.value).
                then { UserPersonalInfo userPersonalInfo ->
                    if (userPersonalInfo == null) {
                        throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoLink.value).exception()
                    }

                    if (userPersonalInfo.type != type) {
                        throw AppErrors.INSTANCE.fieldInvalid(userPersonalInfoLink.value.toString()).exception()
                    }

                    return Promise.pure(userPersonalInfo)
                }
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

    Promise<Void> validateAddresses(User user) {
        if (user.addresses != null) {
            return validateUserPersonalInfoLinkIterator(user.addresses.iterator(),
                    UserPersonalInfoType.ADDRESS.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateEmails(User user) {
        if (user.emails != null) {
            return validateUserPersonalInfoLinkIterator(user.addresses.iterator(),
                    UserPersonalInfoType.EMAIL.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validatePhones(User user) {
        if (user.phones != null) {
            return validateUserPersonalInfoLinkIterator(user.phones.iterator(), UserPersonalInfoType.PHONE.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateName(User user) {
        if (user.name != null) {
            return validateUserPersonalInfoLink(user.name, UserPersonalInfoType.NAME.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateDob(User user) {
        if (user.dob != null) {
            return validateUserPersonalInfoLink(user.dob, UserPersonalInfoType.DOB.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateSMS(User user) {
        if (user.textMessages != null) {
            return validateUserPersonalInfoLinkIterator(user.textMessages.iterator(),
                    UserPersonalInfoType.SMS.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateQQ(User user) {
        if (user.qqs != null) {
            return validateUserPersonalInfoLinkIterator(user.qqs.iterator(), UserPersonalInfoType.QQ.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateWhatsApp(User user) {
        if (user.whatsApps != null) {
            return validateUserPersonalInfoLinkIterator(user.whatsApps.iterator(),
                    UserPersonalInfoType.WHATSAPP.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validatePassport(User user) {
        if (user.passport != null) {
            return validateUserPersonalInfoLink(user.passport, UserPersonalInfoType.PASSPORT.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateGovernmentId(User user) {
        if (user.governmentId != null) {
            return validateUserPersonalInfoLink(user.governmentId, UserPersonalInfoType.GOVERNMENT_ID.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateDriversLicense(User user) {
        if (user.driversLicense != null) {
            return validateUserPersonalInfoLink(user.driversLicense, UserPersonalInfoType.DRIVERS_LICENSE.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateGender(User user) {
        if (user.gender != null) {
            return validateUserPersonalInfoLink(user.gender, UserPersonalInfoType.GENDER.toString())
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
}
