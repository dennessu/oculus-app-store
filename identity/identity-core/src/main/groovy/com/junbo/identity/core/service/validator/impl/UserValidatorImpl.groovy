package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.TimezoneValidator
import com.junbo.identity.core.service.validator.UserValidator
import com.junbo.identity.core.service.validator.UsernameValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class UserValidatorImpl implements UserValidator {

    private UserRepository userRepository

    private UsernameValidator usernameValidator

    private LocaleRepository localeRepository

    private CountryRepository countryRepository

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
                user.canonicalUsername = normalizeService.normalize(user.username)
                if (user.canonicalUsername != oldUser.canonicalUsername) {
                    return userRepository.getUserByCanonicalUsername(user.canonicalUsername).then { User existingUser ->
                        if (existingUser != null) {
                            throw AppErrors.INSTANCE.fieldDuplicate('username').exception()
                        }

                        return Promise.pure(null)
                    }
                }

                return Promise.pure(null)
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
    Promise<Void> validateForSearch(UserListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.username == null && options.groupId == null) {
            throw AppErrors.INSTANCE.parameterRequired('username or groupId').exception()
        }

        if (options.username != null && options.groupId != null) {
            throw AppErrors.INSTANCE.parameterInvalid('username and groupId can\'t search together.').exception()
        }

        return Promise.pure(null)
    }

    private Promise<Void> validateUserInfo(User user) {
        if (user.username != null) {
            if (user.isAnonymous == null) {
                user.isAnonymous = false
            }

            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.fieldInvalid('isAnonymous', "false").exception()
            }
            usernameValidator.validateUsername(user.username)
        } else {
            if (user.isAnonymous == null) {
                user.isAnonymous = true
            }

            if (!user.isAnonymous) {
                throw AppErrors.INSTANCE.fieldInvalid('isAnonymous', "true").exception()
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
            throw AppErrors.INSTANCE.fieldRequired('isAnonymous').exception()
        }

        if (user.status != null) {
            if (!UserStatus.values().any { UserStatus userStatus ->
                return userStatus.toString() == user.status
            }) {
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
        }.then {
            return validateCountryOfResidence(user)
        }
    }

    Promise<Void> validateUserPersonalInfoLinkIterator(User user, Iterator<UserPersonalInfoLink> iter, String type) {
        if (iter.hasNext()) {
            UserPersonalInfoLink userPersonalInfoLink = iter.next();

            if (userPersonalInfoLink.isDefault == null) {
                throw AppErrors.INSTANCE.fieldRequired('isDefault').exception()
            }

            if (userPersonalInfoLink.value == null) {
                throw AppErrors.INSTANCE.fieldRequired('value').exception()
            }

            return userPersonalInfoRepository.get(userPersonalInfoLink.value).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoLink.value).exception()
                }

                if (type != null) {
                    if (userPersonalInfo.type != type) {
                        throw AppErrors.INSTANCE.fieldInvalid(userPersonalInfoLink.value.toString()).exception()
                    }
                }

                if (user.id != userPersonalInfo.userId) {
                    throw AppErrors.INSTANCE.fieldInvalid('userPersonalInfo.value').exception()
                }

                // 2.	User’s default email is required to be globally unique - no two users can use the same email as their default email.
                //      The first user set this email to default will get this email.
                if (userPersonalInfoLink.isDefault && userPersonalInfo.type == UserPersonalInfoType.EMAIL.toString()) {
                    Email email = (Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value, Email)

                    return validateEmailNotUsed(user, email).then {
                        return validateUserPersonalInfoLinkIterator(user, iter, type)
                    }
                }

                return validateUserPersonalInfoLinkIterator(user, iter, type)
            }
        }
        return Promise.pure(null)
    }

    Promise<Void> validateEmailNotUsed(User user, Email email) {
        return userPersonalInfoRepository.searchByEmail(email.info.toLowerCase(Locale.ENGLISH), Integer.MAX_VALUE,
                0).then { List<UserPersonalInfo> existing ->
            if (CollectionUtils.isEmpty(existing)) {
                return Promise.pure(null)
            }

            existing.removeAll { UserPersonalInfo info ->
                return info.userId == user.id
            }

            if (CollectionUtils.isEmpty(existing)) {
                return Promise.pure(null)
            }

            return Promise.each(existing) { UserPersonalInfo info ->
                return userRepository.get(info.userId).then { User existingUser ->
                    if (existingUser == null || CollectionUtils.isEmpty(existingUser.emails)) {
                        return Promise.pure(null)
                    }

                    if (existingUser.emails.any { UserPersonalInfoLink link ->
                        return link.isDefault && link.value == info.id
                    }) {
                        throw AppErrors.INSTANCE.fieldInvalidException('email.info', 'Mail already used.').exception()
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
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
            return localeRepository.get(user.preferredLocale).then { com.junbo.identity.spec.v1.model.Locale locale ->
                if (locale == null) {
                    throw AppErrors.INSTANCE.fieldInvalid(user.preferredLocale.value).exception()
                }

                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }

    private void checkSinglePersonalInfoLink(List<UserPersonalInfoLink> links) {
        if (!CollectionUtils.isEmpty(links)) {
            Collection<UserPersonalInfoLink> defaultLinks = links.findAll { UserPersonalInfoLink link ->
                return link.isDefault
            }
            if (CollectionUtils.isEmpty(defaultLinks)) {
                throw AppErrors.INSTANCE.fieldInvalid('isDefault', 'UserPersonalInfos must have at least one default.').exception()
            }
            if (!CollectionUtils.isEmpty(defaultLinks) && defaultLinks.size() > 1) {
                throw AppErrors.INSTANCE.fieldInvalid('isDefault', 'Can only have one default.').exception()
            }
        }
    }

    Promise<Void> validateAddresses(User user) {
        if (user.addresses != null) {
            checkSinglePersonalInfoLink(user.addresses)
            return validateUserPersonalInfoLinkIterator(user, user.addresses.iterator(),
                    UserPersonalInfoType.ADDRESS.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateEmails(User user) {
        if (user.emails != null) {
            checkSinglePersonalInfoLink(user.emails)
            return validateUserPersonalInfoLinkIterator(user, user.emails.iterator(),
                    UserPersonalInfoType.EMAIL.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validatePhones(User user) {
        if (user.phones != null) {
            checkSinglePersonalInfoLink(user.phones)
            return validateUserPersonalInfoLinkIterator(user, user.phones.iterator(),
                    UserPersonalInfoType.PHONE.toString())
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
            checkSinglePersonalInfoLink(user.textMessages)
            return validateUserPersonalInfoLinkIterator(user, user.textMessages.iterator(),
                    UserPersonalInfoType.SMS.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateQQ(User user) {
        if (user.qqs != null) {
            checkSinglePersonalInfoLink(user.qqs)
            return validateUserPersonalInfoLinkIterator(user, user.qqs.iterator(), UserPersonalInfoType.QQ.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateWhatsApp(User user) {
        if (user.whatsApps != null) {
            checkSinglePersonalInfoLink(user.whatsApps)
            return validateUserPersonalInfoLinkIterator(user, user.whatsApps.iterator(),
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

    Promise<Void> validateCountryOfResidence(User user) {
        if (user.countryOfResidence != null) {
            return countryRepository.get(user.countryOfResidence).then { Country country ->
                if (country == null) {
                    throw AppErrors.INSTANCE.countryNotFound(user.countryOfResidence).exception()
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

    void setCountryRepository(CountryRepository countryRepository) {
        this.countryRepository = countryRepository
    }
}
