package com.junbo.identity.core.service.validator.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.billing.spec.model.VatIdValidationResponse
import com.junbo.billing.spec.resource.VatResource
import com.junbo.common.enumid.CountryId
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
import com.junbo.identity.service.*
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

    private UserService userService

    private UsernameValidator usernameValidator

    private EmailValidator emailValidator

    private LocaleService localeService

    private CountryService countryService

    private UserPersonalInfoService userPersonalInfoService

    private TimezoneValidator timezoneValidator

    private NormalizeService normalizeService

    private VatResource vatResource

    private PaymentInstrumentResource paymentInstrumentResource

    private PITypeService piTypeService

    private Boolean enableVatValidation
    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    private UsernameEmailBlockerService usernameEmailBlockerService

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
        return validateUserInfo(null, user).then {
            if (user.username != null) {
                return validateUserNameDuplicate(user, user).then {
                    return userPersonalInfoService.get(user.username).then { UserPersonalInfo userPersonalInfo ->
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

        return validateUserInfo(oldUser, user).then {
            if (user.username != oldUser.username) {
                return validateUserNameDuplicate(user, oldUser).then {
                    return validateEmailUpdate(user, oldUser)
                }
            }

            return validateEmailUpdate(user, oldUser)
        }.then {
            // https://oculus.atlassian.net/browse/SER-693
            // Will treat username as primary
            return userPersonalInfoService.get(user.username).then { UserPersonalInfo userPersonalInfo ->
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

        return Promise.pure().then {
            if (AuthorizeContext.hasScopes('csr')) {
                return userService.get(userId)
            } else {
                return userService.getNonDeletedUser(userId)
            }
        }.then { User user ->
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

        if (StringUtils.isEmpty(options.email)) {
            if (options.username != null && options.groupId != null) {
                throw AppCommonErrors.INSTANCE.parameterInvalid('username and groupId', 'username and groupId can\'t search together.').exception()
            }

            if (options.username != null && StringUtils.isEmpty(normalizeService.normalize(options.username))) {
                throw AppCommonErrors.INSTANCE.parameterInvalid('username', 'username can\'t be empty').exception()
            }

            if (options.username == null && options.groupId == null) {
                throw AppCommonErrors.INSTANCE.parameterRequired('username or groupId or primaryEmail').exception()
            }
        } else {
            if (!StringUtils.isEmpty(options.username) || options.groupId != null) {
                throw AppCommonErrors.INSTANCE.parameterInvalid('primaryEmail', 'primaryEmail can not search together with username and groupId').exception()
            }
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateEmail(String email) {
        emailValidator.validateEmail(email)

        return userPersonalInfoService.searchByEmail(email.toLowerCase(Locale.ENGLISH), null, Integer.MAX_VALUE, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
            if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                return Promise.pure(null)
            }

            if (userPersonalInfoList.size() >= maximumFetchSize) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('email').exception()
            }

            return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo userPersonalInfo ->
                return userService.getNonDeletedUser(userPersonalInfo.getUserId()).then { User existingUser ->
                    if (existingUser == null || CollectionUtils.isEmpty(existingUser.emails)) {
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

        return userPersonalInfoService.searchByCanonicalUsername(normalizeService.normalize(username), Integer.MAX_VALUE, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
            if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                return Promise.pure(null)
            }

            if (userPersonalInfoList.size() >= maximumFetchSize) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('username').exception()
            }

            return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo userPersonalInfo ->
                return userService.getNonDeletedUser(userPersonalInfo.getUserId()).then { User existingUser ->
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

    // If username or email is empty, throw exception
    // If username has multiple records, the migration data should have error, block this user, return 'BLOCK'
    // If username has no record, email has records, return 'USERNAMEABANDON'
    // If username has no record, email has no records, return 'NEWUSER'
    // If username has record, email is the same, return 'RETURNUSER'
    // If username has record, email is different, return 'ERROR'
    @Override
    Promise<String> validateUsernameEmailBlocker(String username, String email) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('username or email').exception()
        }

        return usernameEmailBlockerService.searchByUsername(normalizeService.normalize(username), Integer.MAX_VALUE, 0).then {
            List<UsernameMailBlocker> blockerList ->
            if (CollectionUtils.isEmpty(blockerList)) {
                return usernameEmailBlockerService.searchByEmail(email.toLowerCase(Locale.ENGLISH), Integer.MAX_VALUE, 0).then {
                    List<UsernameMailBlocker> mailBlockerList ->
                        if (CollectionUtils.isEmpty(mailBlockerList)) {
                            return Promise.pure('NEWUSER')
                        }

                        return Promise.pure('USERNAMEABANDON')
                }
            }
            if (blockerList.size() > 1) {
                return Promise.pure('ERROR')
            }

            UsernameMailBlocker blocker = blockerList.get(0)
            PiiHash piiHash = getPiiHash(UserPersonalInfoType.EMAIL.toString())
            String hashedMail = piiHash.generateHash(email.toLowerCase(Locale.ENGLISH))

            if (blocker.hashEmail.equalsIgnoreCase(hashedMail)) {
                return Promise.pure('RETURNUSER')
            }

            return Promise.pure('ERROR')
        }
    }

    private Promise<Void> validateUserInfo(User oldUser, User newUser) {
        if (newUser.username != null) {
            if (newUser.isAnonymous == null) {
                newUser.isAnonymous = false
            }

            if (newUser.isAnonymous) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('isAnonymous', newUser.isAnonymous, "false").exception()
            }
        } else {
            if (newUser.isAnonymous == null) {
                newUser.isAnonymous = true
            }

            if (!newUser.isAnonymous) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('isAnonymous', newUser.isAnonymous, "true").exception()
            }
        }

        if (newUser.preferredTimezone != null && oldUser?.preferredTimezone != newUser.preferredTimezone) {
            if (!timezoneValidator.isValidTimezone(newUser.preferredTimezone)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('preferredTimezone').exception()
            }
        } else {
            newUser.preferredTimezone = timezoneValidator.defaultTimezone
        }

        if (newUser.status != null && oldUser?.status != newUser.status) {
            if (!UserStatus.values().any { UserStatus userStatus ->
                return userStatus.toString() == newUser.status
            }) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('status').exception()
            }
        }

        return validateUserName(oldUser, newUser).then {
                return validateLocale(oldUser, newUser)
            }.then {
                return validateAddresses(oldUser, newUser)
            }.then {
                return validateEmails(oldUser, newUser)
            }.then {
                return validatePhones(oldUser, newUser)
            }.then {
                return validateName(oldUser, newUser)
            }.then {
                return validateDob(oldUser, newUser)
            }.then {
                return validateSMS(oldUser, newUser)
            }.then {
                return validateQQ(oldUser, newUser)
            }.then {
                return validateWhatsApp(oldUser, newUser)
            }.then {
                return validatePassport(oldUser, newUser)
            }.then {
                return validateGovernmentId(oldUser, newUser)
            }.then {
                return validateDriversLicense(oldUser, newUser)
            }.then {
                return validateGender(oldUser, newUser)
            }.then {
                return validateCountryOfResidence(oldUser, newUser)
            }.then {
                return validateVat(newUser)
            }.then {
                return validateTaxExemption(oldUser, newUser)
            }.then {
                return validateDefaultPI(oldUser, newUser)
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

            return userPersonalInfoService.get(userPersonalInfoLink.value).then { UserPersonalInfo userPersonalInfo ->
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
        return userPersonalInfoService.searchByEmail(email.info.toLowerCase(Locale.ENGLISH), null, Integer.MAX_VALUE,
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
                return userService.getNonDeletedUser(info.userId).then { User existingUser ->
                    if (existingUser == null || CollectionUtils.isEmpty(existingUser.emails)) {
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
        return userPersonalInfoService.get(userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
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

    Promise<Void> validateUserName(User oldUser, User newUser) {
        if (isUserPIISame(oldUser?.username, newUser?.username)) {
            return Promise.pure(null)
        }
        if (newUser.username != null) {
            return userPersonalInfoService.get(newUser.username).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    throw AppErrors.INSTANCE.userPersonalInfoNotFound(newUser.username).exception()
                }

                if (userPersonalInfo.type != UserPersonalInfoType.USERNAME.toString()) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('user.username', 'UserPersonalInfo must be USERNAME type').exception()
                }

                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }

    Promise<Void> validateLocale(User oldUser, User newUser) {
        if (oldUser?.preferredLocale == newUser?.preferredLocale) {
            return Promise.pure(null)
        }
        if (newUser.preferredLocale != null) {
            return localeService.get(newUser.preferredLocale).then { com.junbo.identity.spec.v1.model.Locale locale ->
                if (locale == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid(newUser.preferredLocale.value).exception()
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

    Promise<Void> validateAddresses(User oldUser, User newUser) {
        if (isUserPIILinkListSame(oldUser?.addresses, newUser?.addresses)) {
            return Promise.pure(null)
        }
        if (newUser.addresses != null) {
            checkSinglePersonalInfoLink(newUser.addresses)
            return validateUserPersonalInfoLinkIterator(newUser, newUser.addresses.iterator(),
                    UserPersonalInfoType.ADDRESS.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateEmails(User oldUser, User user) {
        if (isUserPIILinkListSame(oldUser?.emails, user?.emails)) {
            return Promise.pure(null)
        }
        if (user.emails != null) {
            checkSinglePersonalInfoLink(user.emails)
            return validateUserPersonalInfoLinkIterator(user, user.emails.iterator(),
                    UserPersonalInfoType.EMAIL.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validatePhones(User oldUser, User newUser) {
        if (isUserPIILinkListSame(oldUser?.phones, newUser?.phones)) {
            return Promise.pure(null)
        }
        if (newUser.phones != null) {
            checkSinglePersonalInfoLink(newUser.phones)
            return validateUserPersonalInfoLinkIterator(newUser, newUser.phones.iterator(),
                    UserPersonalInfoType.PHONE.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateName(User oldUser, User newUser) {
        if (isUserPIISame(oldUser?.name, newUser?.name)) {
            return Promise.pure(null)
        }
        if (newUser.name != null) {
            return validateUserPersonalInfoId(newUser, newUser.name, UserPersonalInfoType.NAME.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateDob(User oldUser, User newUser) {
        if (isUserPIISame(oldUser?.name, newUser?.name)) {
            return Promise.pure(null)
        }
        if (newUser.dob != null) {
            return validateUserPersonalInfoId(newUser, newUser.dob, UserPersonalInfoType.DOB.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateSMS(User oldUser, User newUser) {
        if (isUserPIILinkListSame(oldUser?.textMessages, newUser?.textMessages)) {
            return Promise.pure(null)
        }
        if (newUser.textMessages != null) {
            checkSinglePersonalInfoLink(newUser.textMessages)
            return validateUserPersonalInfoLinkIterator(newUser, newUser.textMessages.iterator(),
                    UserPersonalInfoType.SMS.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateQQ(User oldUser, User newUser) {
        if (isUserPIILinkListSame(oldUser?.qqs, newUser?.qqs)) {
            return Promise.pure(null)
        }
        if (newUser.qqs != null) {
            checkSinglePersonalInfoLink(newUser.qqs)
            return validateUserPersonalInfoLinkIterator(newUser, newUser.qqs.iterator(), UserPersonalInfoType.QQ.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateWhatsApp(User oldUser, User newUser) {
        if (isUserPIILinkListSame(oldUser?.whatsApps, newUser?.whatsApps)) {
            return Promise.pure(null)
        }
        if (newUser.whatsApps != null) {
            checkSinglePersonalInfoLink(newUser.whatsApps)
            return validateUserPersonalInfoLinkIterator(newUser, newUser.whatsApps.iterator(),
                    UserPersonalInfoType.WHATSAPP.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validatePassport(User oldUser, User newUser) {
        if (isUserPIISame(oldUser?.passport, newUser?.passport)) {
            return Promise.pure(null)
        }
        if (newUser.passport != null) {
            return validateUserPersonalInfoId(newUser, newUser.passport, UserPersonalInfoType.PASSPORT.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateGovernmentId(User oldUser, User newUser) {
        if (isUserPIISame(oldUser?.governmentId, newUser?.governmentId)) {
            return Promise.pure(null)
        }
        if (newUser.governmentId != null) {
            return validateUserPersonalInfoId(newUser, newUser.governmentId, UserPersonalInfoType.GOVERNMENT_ID.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateDriversLicense(User oldUser, User newUser) {
        if (isUserPIISame(oldUser?.driversLicense, newUser?.driversLicense)) {
            return Promise.pure(null)
        }
        if (newUser.driversLicense != null) {
            return validateUserPersonalInfoId(newUser, newUser.driversLicense, UserPersonalInfoType.DRIVERS_LICENSE.toString())
        }
        return Promise.pure(null)
    }

    Promise<Void> validateGender(User oldUser, User newUser) {
        if (isUserPIISame(oldUser?.gender, newUser?.gender)) {
            return Promise.pure(null)
        }
        if (newUser.gender != null) {
            return validateUserPersonalInfoId(newUser, newUser.gender, UserPersonalInfoType.GENDER.toString())
        }

        return Promise.pure(null)
    }

    Promise<Void> validateCountryOfResidence(User oldUser, User newUser) {
        if (oldUser?.countryOfResidence != newUser?.countryOfResidence) {
            return Promise.pure(null)
        }
        if (newUser.countryOfResidence != null) {
            return countryService.get(newUser.countryOfResidence).then { Country country ->
                if (country == null) {
                    throw AppErrors.INSTANCE.countryNotFound(newUser.countryOfResidence).exception()
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

    Promise<Void> validateTaxExemption(User oldUser, User user) {
        if (user.taxExemption == null || user.taxExemption.isEmpty()) {
            return Promise.pure(null)
        }
        Map<String, TaxExempt> taxExemptMap = oldUser?.taxExemption

        return Promise.each(user.taxExemption.entrySet()) { Map.Entry<String, TaxExempt> entry ->
            if (StringUtils.isEmpty(entry.key) ||!ValidatorUtil.isValidCountryCode(entry.key)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('taxExemption.key').exception()
            }

            if (entry.value == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('taxExemption.value').exception()
            }
            return countryService.get(new CountryId(entry.getKey())).then { Country country ->
                if (country == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('taxExemption.key').exception()
                }

                TaxExempt existingTaxExempt = taxExemptMap?.get(entry.key)
                if (existingTaxExempt != null && existingTaxExempt.equals(entry.value)) {
                    return Promise.pure(null)
                }

                if (existingTaxExempt?.isTaxExemptionValidated != entry.value.isTaxExemptionValidated) {
                    if (!AuthorizeContext.hasScopes('csr')) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('taxExemption.value', 'isTaxExemptionValidated can only be changed by CSR').exception()
                    }
                }

                Date startDate = existingTaxExempt?.taxExemptionStartDate
                Date endDate = existingTaxExempt?.taxExemptionEndDate
                if (startDate != null && endDate != null && startDate.after(endDate)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('taxExemption.taxExemptionStartDate', 'startDate should be earlier than endDate').exception()
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(null)
        }
    }

    Promise<Map<String, TaxExempt>> getExistingTaxExempt(User user) {
        if (user.getId() == null) {
            return Promise.pure(new HashMap<String, TaxExempt>())
        } else {
            return userService.get(user.getId()).then { User existing ->
                if (existing.taxExemption == null || existing.taxExemption.isEmpty()) {
                    return Promise.pure(new HashMap<String, TaxExempt>())
                }

                return Promise.pure(existing.taxExemption)
            }
        }
    }

    Promise<Void> validateDefaultPI(User oldUser, User newUser) {
        if (oldUser?.defaultPI == newUser?.defaultPI) {
            return Promise.pure(null)
        }
        if (newUser.defaultPI == null) {
            return Promise.pure(null)
        }

        return paymentInstrumentResource.getById(newUser.defaultPI).then { PaymentInstrument pi ->
            if (pi ==  null) {
                throw AppErrors.INSTANCE.paymentInstrumentNotFound(newUser.defaultPI).exception()
            }

            return piTypeService.get(new PITypeId(pi.getType())).then { PIType piType ->
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

        return userPersonalInfoService.get(user.username).then { UserPersonalInfo userPersonalInfo ->
            UserLoginName loginName = (UserLoginName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
            return userPersonalInfoService.searchByCanonicalUsername(loginName.canonicalUsername, Integer.MAX_VALUE, 0).then { List<UserPersonalInfo> userPersonalInfoList ->
                if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                    return Promise.pure(null)
                }

                if (userPersonalInfoList.size() >= maximumFetchSize) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('username').exception()
                }

                List<UserPersonalInfo> userPersonalInfos = new ArrayList<>()
                return Promise.each(userPersonalInfoList.iterator()) { UserPersonalInfo personalInfo ->
                    return userService.getNonDeletedUser(personalInfo.userId).then { User existing ->
                        if (existing != null && existing.username != null && existing.username == personalInfo.getId() && existing.getId() != oldUser.getId()) {
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

        return userPersonalInfoService.get(oldLink.getValue()).then { UserPersonalInfo oldPII ->
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

            return userPersonalInfoService.get(link.value).then { UserPersonalInfo pii ->
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

    private boolean isUserPIISame(UserPersonalInfoId oldPIIId, UserPersonalInfoId piiId) {
        if (oldPIIId != piiId) {
            return false
        }

        return true
    }

    private boolean isUserPIILinkListSame(List<UserPersonalInfoLink> oldLinks, List<UserPersonalInfoLink> links) {
        if (CollectionUtils.isEmpty(oldLinks) && CollectionUtils.isEmpty(links)) {
            return true
        }

        if (CollectionUtils.isEmpty(oldLinks) || CollectionUtils.isEmpty(links)) {
            return false
        }

        if (oldLinks.size() != links.size()) {
            return false
        }

        boolean sameLinkList = true

        oldLinks.each { UserPersonalInfoLink link ->
            UserPersonalInfoLink existing = links.find { UserPersonalInfoLink newLink ->
                return newLink == link
            }

            if (existing == null) {
                sameLinkList = false
            }
        }

        links.each { UserPersonalInfoLink link ->
            UserPersonalInfoLink existing = links.find { UserPersonalInfoLink newLink ->
                return newLink == link
            }

            if (existing == null) {
                sameLinkList = false
            }
        }
        return sameLinkList
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
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
    void setLocaleService(LocaleService localeService) {
        this.localeService = localeService
    }

    @Required
    void setUserPersonalInfoService(UserPersonalInfoService userPersonalInfoService) {
        this.userPersonalInfoService = userPersonalInfoService
    }

    @Required
    void setCountryService(CountryService countryService) {
        this.countryService = countryService
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
    void setPiTypeService(PITypeService piTypeService) {
        this.piTypeService = piTypeService
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
    void setUsernameEmailBlockerService(UsernameEmailBlockerService usernameEmailBlockerService) {
        this.usernameEmailBlockerService = usernameEmailBlockerService
    }

    @Required
    void setPiiHashFactory(PiiHashFactory piiHashFactory) {
        this.piiHashFactory = piiHashFactory
    }
}
