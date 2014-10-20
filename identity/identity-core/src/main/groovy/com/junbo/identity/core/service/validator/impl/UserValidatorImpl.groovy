package com.junbo.identity.core.service.validator.impl

import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.billing.spec.resource.VatResource
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.PITypeId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.EmailValidator
import com.junbo.identity.core.service.validator.TimezoneValidator
import com.junbo.identity.core.service.validator.UserValidator
import com.junbo.identity.core.service.validator.UsernameValidator
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.hash.PiiHashFactory
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.*
import com.junbo.identity.data.repository.migration.UsernameEmailBlockerRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import java.util.Locale

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class UserValidatorImpl implements UserValidator {

    private UserRepository userRepository

    private UsernameValidator usernameValidator

    private EmailValidator emailValidator

    private LocaleRepository localeRepository

    private CountryRepository countryRepository

    private UserPersonalInfoRepository userPersonalInfoRepository

    private TimezoneValidator timezoneValidator

    private NormalizeService normalizeService

    private VatResource vatResource

    private PaymentInstrumentResource paymentInstrumentResource

    private PITypeRepository piTypeRepository

    private Boolean enableVatValidation
    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    private UsernameEmailBlockerRepository usernameEmailBlockerRepository

    private PiiHashFactory piiHashFactory

    @Override
    Promise<Void> validateForCreate(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        if (user.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        if (user.status == null) {
            user.status = UserStatus.ACTIVE.toString()
        }
        return validateUserInfo(user).then {
            if (user.username != null) {
                return validateUserNameDuplicate(user, user).then {
                    return userPersonalInfoRepository.get(user.username).then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null || userPersonalInfo.value == null) {
                            return Promise.pure(null)
                        }

                        UserLoginName userLoginName = (UserLoginName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
                        user.nickName = userLoginName.userName
                        return Promise.pure(null)
                    }
                }

            }

            // https://oculus.atlassian.net/browse/SER-693
            // Will treat username as primary
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
            throw AppCommonErrors.INSTANCE.fieldRequired('status').exception()
        }

        if (oldUser.status == UserStatus.DELETED.toString()) {
            throw AppCommonErrors.INSTANCE.invalidOperation('Can\'t update delete user').exception()
        }

        if (user.status == UserStatus.DELETED.toString()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('status', 'Can\'t update user status to Delete').exception()
        }

        if (user.isAnonymous == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('isAnonymous').exception()
        }

        return validateUserInfo(user).then {
            if (user.username != oldUser.username) {
                return validateUserNameDuplicate(user, oldUser).then {
                    return validateEmailUpdate(user, oldUser)
                }
            }

            return validateEmailUpdate(user, oldUser)
        }.then {
            // https://oculus.atlassian.net/browse/SER-693
            // Will treat username as primary
            return userPersonalInfoRepository.get(user.username).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null || userPersonalInfo.value == null) {
                    return Promise.pure(null)
                }

                UserLoginName userLoginName = (UserLoginName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
                user.nickName = userLoginName.userName

                return Promise.pure(null)
            }
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
            throw AppCommonErrors.INSTANCE.parameterRequired('username or groupId').exception()
        }

        if (options.username != null && options.groupId != null) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('username and groupId', 'username and groupId can\'t search together.').exception()
        }

        if (options.username != null && StringUtils.isEmpty(normalizeService.normalize(options.username))) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('username', 'username can\'t be empty').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateEmail(String email) {
        emailValidator.validateEmail(email)

        return userPersonalInfoRepository.searchByEmail(email.toLowerCase(Locale.ENGLISH), null, Integer.MAX_VALUE, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
            if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                return Promise.pure(null)
            }

            if (userPersonalInfoList.size() >= maximumFetchSize) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('email').exception()
            }

            return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo userPersonalInfo ->
                return userRepository.get(userPersonalInfo.getUserId()).then { User existingUser ->
                    if (CollectionUtils.isEmpty(existingUser.emails)) {
                        return Promise.pure(null)
                    }

                    UserPersonalInfoLink link = existingUser.emails.find { UserPersonalInfoLink personalInfoLink ->
                        return personalInfoLink.isDefault && personalInfoLink.value == userPersonalInfo.getId()
                    }

                    if (link != null) {
                        throw AppCommonErrors.INSTANCE.fieldDuplicate('email').exception()
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateUsername(String username) {
        usernameValidator.validateUsername(username);

        return userPersonalInfoRepository.searchByCanonicalUsername(normalizeService.normalize(username), Integer.MAX_VALUE, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
            if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                return Promise.pure(null)
            }

            if (userPersonalInfoList.size() >= maximumFetchSize) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('username').exception()
            }

            return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo userPersonalInfo ->
                return userRepository.get(userPersonalInfo.getUserId()).then { User existingUser ->
                    if (existingUser.username == userPersonalInfo.getId()) {
                        throw AppCommonErrors.INSTANCE.fieldDuplicate('username').exception()
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Boolean> validateUsernameEmailBlocker(String username, String email) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            return Promise.pure(true)
        }

        return usernameEmailBlockerRepository.searchByUsername(normalizeService.normalize(username), Integer.MAX_VALUE, 0).then {
            List<UsernameMailBlocker> blockerList ->
            if (CollectionUtils.isEmpty(blockerList)) {
                return Promise.pure(false)
            }
            if (blockerList.size() > 1) {
                return Promise.pure(true)
            }

            UsernameMailBlocker blocker = blockerList.get(0)
            PiiHash piiHash = getPiiHash(UserPersonalInfoType.EMAIL.toString())
            String hashedMail = piiHash.generateHash(email.toLowerCase(Locale.ENGLISH))

            if (blocker.hashEmail.equalsIgnoreCase(hashedMail)) {
                return Promise.pure(false)
            }

            return Promise.pure(true)
        }
    }

    private Promise<Void> validateUserInfo(User user) {
        if (user.username != null) {
            if (user.isAnonymous == null) {
                user.isAnonymous = false
            }

            if (user.isAnonymous) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('isAnonymous', user.isAnonymous, "false").exception()
            }
        } else {
            if (user.isAnonymous == null) {
                user.isAnonymous = true
            }

            if (!user.isAnonymous) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('isAnonymous', user.isAnonymous, "true").exception()
            }
        }

        if (user.preferredTimezone != null) {
            if (!timezoneValidator.isValidTimezone(user.preferredTimezone)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('preferredTimezone').exception()
            }
        } else {
            user.preferredTimezone = timezoneValidator.defaultTimezone
        }

        if (user.isAnonymous == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('isAnonymous').exception()
        }

        if (user.status != null) {
            if (!UserStatus.values().any { UserStatus userStatus ->
                return userStatus.toString() == user.status
            }) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('status').exception()
            }
        }

        return validateUserName(user).then {
                return validateLocale(user)
            }.then {
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
            }.then {
                return validateVat(user)
            }.then {
                return validateDefaultPI(user)
            }
    }

    Promise<Void> validateUserPersonalInfoLinkIterator(User user, Iterator<UserPersonalInfoLink> iter, String type) {
        if (iter.hasNext()) {
            UserPersonalInfoLink userPersonalInfoLink = iter.next();

            if (userPersonalInfoLink.isDefault == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('isDefault').exception()
            }

            if (userPersonalInfoLink.value == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('value').exception()
            }

            return userPersonalInfoRepository.get(userPersonalInfoLink.value).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoLink.value).exception()
                }

                if (type != null) {
                    if (userPersonalInfo.type != type) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid(userPersonalInfoLink.value.toString()).exception()
                    }
                }

                if (user.id != userPersonalInfo.userId) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('userPersonalInfo.value').exception()
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
        return userPersonalInfoRepository.searchByEmail(email.info.toLowerCase(Locale.ENGLISH), null, Integer.MAX_VALUE,
                0).then { List<UserPersonalInfo> existing ->
            if (CollectionUtils.isEmpty(existing)) {
                return Promise.pure(null)
            }

            if (existing.size() >= maximumFetchSize) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('email').exception()
            }

            existing.removeAll { UserPersonalInfo info ->
                return info.userId == user.id
            }

            if (CollectionUtils.isEmpty(existing)) {
                return Promise.pure(null)
            }

            return Promise.each(existing) { UserPersonalInfo info ->
                return userRepository.get(info.userId).then { User existingUser ->
                    if (existingUser == null || CollectionUtils.isEmpty(existingUser.emails) || existingUser.status == UserStatus.DELETED.toString()) {
                        return Promise.pure(null)
                    }

                    if (existingUser.emails.any { UserPersonalInfoLink link ->
                        return link.isDefault && link.value == info.id
                    }) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('email.info', 'Mail already used.').exception()
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
        }
    }

    Promise<Void> validateUserPersonalInfoId(User user, UserPersonalInfoId userPersonalInfoId, String type) {
        if (userPersonalInfoId == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('userPersonalInfoId is null').exception()
        }
        return userPersonalInfoRepository.get(userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoId).exception()
            }

            if (userPersonalInfo.userId != user.getId()) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('userPersonalInfoId', 'userPersonalInfoId : ' +
                        userPersonalInfo.getId() + ' doesn\'t belong to current user.').exception()
            }

            if (userPersonalInfo.type != type) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(userPersonalInfoId.toString()).exception()
            }

            return Promise.pure(userPersonalInfo)
        }
    }

    Promise<Void> validateUserName(User user) {
        if (user.username != null) {
            return userPersonalInfoRepository.get(user.username).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    throw AppErrors.INSTANCE.userPersonalInfoNotFound(user.username).exception()
                }

                if (userPersonalInfo.type != UserPersonalInfoType.USERNAME.toString()) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('user.username', 'UserPersonalInfo must be USERNAME type').exception()
                }

                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }

    Promise<Void> validateLocale(User user) {
        if (user.preferredLocale != null) {
            return localeRepository.get(user.preferredLocale).then { com.junbo.identity.spec.v1.model.Locale locale ->
                if (locale == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid(user.preferredLocale.value).exception()
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
                throw AppCommonErrors.INSTANCE.fieldInvalid('isDefault', 'UserPersonalInfos must have at least one default.').exception()
            }
            if (!CollectionUtils.isEmpty(defaultLinks) && defaultLinks.size() > 1) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('isDefault', 'Can only have one default.').exception()
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
            return validateUserPersonalInfoId(user, user.name, UserPersonalInfoType.NAME.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateDob(User user) {
        if (user.dob != null) {
            return validateUserPersonalInfoId(user, user.dob, UserPersonalInfoType.DOB.toString())
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
            return validateUserPersonalInfoId(user, user.passport, UserPersonalInfoType.PASSPORT.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateGovernmentId(User user) {
        if (user.governmentId != null) {
            return validateUserPersonalInfoId(user, user.governmentId, UserPersonalInfoType.GOVERNMENT_ID.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateDriversLicense(User user) {
        if (user.driversLicense != null) {
            return validateUserPersonalInfoId(user, user.driversLicense, UserPersonalInfoType.DRIVERS_LICENSE.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateGender(User user) {
        if (user.gender != null) {
            return validateUserPersonalInfoId(user, user.gender, UserPersonalInfoType.GENDER.toString())
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

    Promise<Void> validateVat(User user) {
        if (user.vat == null || user.vat.isEmpty() || !enableVatValidation) {
            return Promise.pure(null)
        }

        user.vat.each { Map.Entry<String, UserVAT> entry ->
            if (!ValidatorUtil.isValidCountryCode(entry.key)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('vat.key').exception()
            }
        }

        return Promise.each(user.vat.entrySet()) { Map.Entry<String, UserVAT> entry ->
            UserVAT vat = entry.value
            return vatResource.validateVatId(vat.vatNumber, entry.key).then { VatIdValidationResponse response ->
                if (response.status == 'VALID') {
                    vat.lastValidateTime = new Date()
                } else if (response.status == 'INVALID') {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('vat.value', vat.vatNumber + ' isn\'t valid').exception()
                } else if (response.status == 'SERVICE_UNAVAILABLE' || response.status == 'UNKNOWN') {
                    // do nothing here.
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(null)
        }
    }

    Promise<Void> validateDefaultPI(User user) {
        if (user.defaultPI == null) {
            return Promise.pure(null)
        }

        return paymentInstrumentResource.getById(user.defaultPI).then { PaymentInstrument pi ->
            if (pi ==  null) {
                throw AppErrors.INSTANCE.paymentInstrumentNotFound(user.defaultPI).exception()
            }

            return piTypeRepository.get(new PITypeId(pi.getType())).then { PIType piType ->
                if (piType == null) {
                    throw AppErrors.INSTANCE.piTypeNotFound(new PITypeId(pi.getType())).exception()
                }

                if (piType.typeCode != com.junbo.common.id.PIType.STOREDVALUE.toString()
                 && piType.typeCode != com.junbo.common.id.PIType.CREDITCARD.toString()) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('defaultPI', 'defaultPI can only support ' +
                            'STOREDVALUE and CREDITCARD').exception()
                }

                return Promise.pure(null)
            }
        }
    }

    Promise<Void> validateUserNameDuplicate(User user, User oldUser) {
        if (oldUser == null) {
            throw new IllegalArgumentException('oldUser is null')
        }

        if (user.username == null) { // do need to check duplicate if username is null.
            return Promise.pure()
        }

        return userPersonalInfoRepository.get(user.username).then { UserPersonalInfo userPersonalInfo ->
            UserLoginName loginName = (UserLoginName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
            return userPersonalInfoRepository.searchByCanonicalUsername(loginName.canonicalUsername, Integer.MAX_VALUE, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
                if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                    return Promise.pure(null)
                }

                if (userPersonalInfoList.size() >= maximumFetchSize) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('username').exception()
                }

                List<UserPersonalInfo> userPersonalInfos = new ArrayList<>()
                return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo personalInfo ->
                    return userRepository.get(personalInfo.userId).then { User existing ->
                        if (existing != null && existing.username != null && existing.username == personalInfo.getId() && existing.getId() != oldUser.getId()
                         && existing.status != UserStatus.DELETED.toString()) {
                            userPersonalInfos.add(personalInfo)
                            return Promise.pure(Promise.BREAK)
                        }

                        return Promise.pure(null)
                    }
                }.then {
                    if (!CollectionUtils.isEmpty(userPersonalInfos)) {
                        throw AppCommonErrors.INSTANCE.fieldDuplicate('username').exception()
                    }

                    return Promise.pure(null)
                }
            }
        }
    }

    // Only validated email can update validated email
    Promise<Void> validateEmailUpdate(User user, User oldUser) {
        if (CollectionUtils.isEmpty(oldUser.emails)) {
            return Promise.pure(null)
        }

        UserPersonalInfoLink oldLink = oldUser.emails.find { UserPersonalInfoLink oldLink ->
            return oldLink.isDefault
        }

        if (oldLink == null) {
            return Promise.pure(null)
        }

        return userPersonalInfoRepository.get(oldLink.getValue()).then { UserPersonalInfo oldPII ->
            if (oldPII == null || oldPII.lastValidateTime == null) {
                return Promise.pure(null)
            }

            if (CollectionUtils.isEmpty(user.emails)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('emails', 'Only validated emails can update validated emails').exception()
            }

            UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink infoLink ->
                return infoLink.isDefault
            }

            if (link == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('emails', 'No default emails found').exception()
            }

            if (oldLink.value == link.value) {
                return Promise.pure(null)
            }

            return userPersonalInfoRepository.get(link.value).then { UserPersonalInfo pii ->
                if (pii == null || pii.lastValidateTime == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('emails', 'Only validated emails can update validated emails').exception()
                }

                return Promise.pure(null)
            }
        }
    }

    // todo:    Need to add this method to util
    private PiiHash getPiiHash(String type) {
        PiiHash hash = piiHashFactory.getAllPiiHashes().find { PiiHash piiHash ->
            return piiHash.handles(type)
        }
        if (hash == null) {
            throw new IllegalStateException('No hash implementation for type ' + type)
        }

        return hash
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
    void setEmailValidator(EmailValidator emailValidator) {
        this.emailValidator = emailValidator
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

    @Required
    void setVatResource(VatResource vatResource) {
        this.vatResource = vatResource
    }

    @Required
    void setPaymentInstrumentResource(PaymentInstrumentResource paymentInstrumentResource) {
        this.paymentInstrumentResource = paymentInstrumentResource
    }

    @Required
    void setPiTypeRepository(PITypeRepository piTypeRepository) {
        this.piTypeRepository = piTypeRepository
    }

    @Required
    void setEnableVatValidation(Boolean enableVatValidation) {
        this.enableVatValidation = enableVatValidation
    }

    @Required
    void setMaximumFetchSize(Integer maximumFetchSize) {
        this.maximumFetchSize = maximumFetchSize
    }

    @Required
    void setUsernameEmailBlockerRepository(UsernameEmailBlockerRepository usernameEmailBlockerRepository) {
        this.usernameEmailBlockerRepository = usernameEmailBlockerRepository
    }

    @Required
    void setPiiHashFactory(PiiHashFactory piiHashFactory) {
        this.piiHashFactory = piiHashFactory
    }
}
