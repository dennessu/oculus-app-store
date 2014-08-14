package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.util.CodeGenerator
import com.junbo.identity.core.service.validator.UserTFAValidator
import com.junbo.identity.data.identifiable.TFASearchType
import com.junbo.identity.data.identifiable.TFAVerifyType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.*
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserTFAListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class UserTFAValidatorImpl implements UserTFAValidator {
    private static final Integer SECONDS_PER_HOUR = 3600
    private Integer maxTFACodeExpireTime
    private Integer maxSMSRequestsPerHour
    private Integer maxReuseSeconds

    private CodeGenerator codeGenerator

    private UserRepository userRepository
    private UserTFAPhoneRepository userTFAPhoneRepository
    private UserTFAMailRepository userTFAMailRepository
    private UserPersonalInfoRepository userPersonalInfoRepository
    private LocaleRepository localeRepository

    private Integer minTemplateLength
    private Integer maxTemplateLength

    @Override
    Promise<UserTFA> validateForGet(UserId userId, UserTFAId userTFAId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTFAId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTFAId').exception()
        }

        return userRepository.get(userId).then { User existing ->
            if (existing == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (existing.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (existing.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTFAPhoneRepository.get(userTFAId).then { UserTFA existingUserTFACode ->
                if (existingUserTFACode == null) {
                    throw AppErrors.INSTANCE.userTFANotFound(userTFAId).exception()
                }

                if (existingUserTFACode.userId != userId) {
                    throw AppCommonErrors.INSTANCE.parameterInvalid('userId', 'userTFACode.userId and userId doesn\'t match.').exception()
                }

                return Promise.pure(existingUserTFACode)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserTFAListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (options.personalInfo == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('personalInfo').exception()
        }

        if (options.type == null) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('type').exception()
        }

        List<String> allowedSearchTypes = TFASearchType.values().collect { TFASearchType searchType ->
            searchType.toString()
        }
        if (!(options.type in allowedSearchTypes)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('verifyType', allowedSearchTypes.join(',')).exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTFA userTFA) {

        if (userTFA.verifyCode != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('verifyCode').exception()
        }

        return basicTFACheck(userId, userTFA).then {
            return fillCode(userId, userTFA)
        }.then {
            if (userTFA.id != null) {
                throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
            }

            if (userTFA.expiresBy != null) {
                throw AppCommonErrors.INSTANCE.fieldMustBeNull('expiresBy').exception()
            }

            if (userTFA.active != null) {
                throw AppCommonErrors.INSTANCE.fieldMustBeNull('active').exception()
            }

            Calendar cal = Calendar.instance
            cal.setTime(new Date())
            cal.add(Calendar.SECOND, maxTFACodeExpireTime)
            userTFA.expiresBy = cal.time

            userTFA.active = true
            userTFA.userId = userId

            return tfaAdvanceCheck(userTFA)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTFAId userTFAId, UserTFA userTFA, UserTFA oldUserTFA) {

        if (userTFA.verifyCode != null && userTFA.verifyCode != oldUserTFA.verifyCode) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('verifyCode').exception()
        }

        userTFA.verifyCode = oldUserTFA.verifyCode
        return basicTFACheck(userId, userTFA).then {
            return userTFAPhoneRepository.get(userTFAId).then { UserTFA tfa ->
                if (tfa == null) {
                    throw AppErrors.INSTANCE.userTFANotFound(userTFAId).exception()
                }

                return Promise.pure(null)
            }
        }.then {
            if (userTFA.id == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
            }

            if (userTFA.expiresBy == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('expiresBy').exception()
            }

            if (userTFA.active == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('active').exception()
            }

            // TeleCode can't update tfa number & verifyCode & userId
            if (userTFA.personalInfo != oldUserTFA.personalInfo) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('personalInfo').exception()
            }

            if (userTFA.userId != userId) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userTFA.userId, userId).exception()
            }

            if (userTFA.userId != oldUserTFA.userId) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userTFA.userId, oldUserTFA.userId).exception()
            }

            if (userTFA.verifyCode != oldUserTFA.verifyCode) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('verifyCode', userTFA.verifyCode, oldUserTFA.verifyCode).exception()
            }

            return Promise.pure(null)
        }
    }

    // todo:    Liangfu:    Later if we have another tfa type, we can split them and use singleton

    private Promise<Void> tfaAdvanceCheck(UserTFA userTFA) {
        if (userTFA.verifyType == TFAVerifyType.MAIL.toString()) {
            return tfaMailAdvanceCheck(userTFA)
        } else {
            return tfaPhoneAdvanceCheck(userTFA)
        }
    }

    private Promise<Void> tfaMailAdvanceCheck(UserTFA userTFA) {
        return userTFAMailRepository.searchTFACodeByUserIdAndPIIAfterTime(userTFA.userId, userTFA.personalInfo,
                Integer.MAX_VALUE, 0, getTimeStartOffset(SECONDS_PER_HOUR)).then { List<UserTFA> userTFAList ->
            if (CollectionUtils.isEmpty(userTFAList) || userTFAList.size() <= maxSMSRequestsPerHour) {
                return Promise.pure(null)
            }

            throw AppCommonErrors.INSTANCE.fieldInvalid('userId', 'Reach maximum request number per hour').exception()
        }
    }

    private Promise<Void> tfaPhoneAdvanceCheck(UserTFA userTFA) {
        return userTFAPhoneRepository.searchTFACodeByUserIdAndPIIAfterTime(userTFA.userId, userTFA.personalInfo,
                Integer.MAX_VALUE, 0, getTimeStartOffset(SECONDS_PER_HOUR)).then { List<UserTFA> userTeleCodeList ->
            if (CollectionUtils.isEmpty(userTeleCodeList) || userTeleCodeList.size() <= maxSMSRequestsPerHour) {
                return Promise.pure(null)
            }

            throw AppCommonErrors.INSTANCE.fieldInvalid('userId', 'Reach maximum request number per hour').exception()
        }
    }

    private Promise<Void> fillCode(UserId userId, UserTFA userTFA) {
        if (userTFA.verifyType == TFAVerifyType.MAIL.toString()) {
            return fillEmailCode(userId, userTFA)
        } else {
            return fillPhoneCode(userId, userTFA)
        }
    }

    private Promise<Void> fillEmailCode(UserId userId, UserTFA userTFA) {
        return userTFAMailRepository.searchTFACodeByUserIdAndPIIAfterTime(userId, userTFA.personalInfo,
            Integer.MAX_VALUE, 0, getTimeStartOffset(maxReuseSeconds)).then { List<UserTFA> codeList ->
            if (CollectionUtils.isEmpty(codeList)) {
                userTFA.verifyCode = codeGenerator.generateCode()
            } else {
                userTFA.verifyCode = codeList.get(0).verifyCode
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> fillPhoneCode(UserId userId, UserTFA userTFA) {
        return userTFAPhoneRepository.searchTFACodeByUserIdAndPIIAfterTime(userId, userTFA.personalInfo,
                Integer.MAX_VALUE, 0, getTimeStartOffset(maxReuseSeconds)).then { List<UserTFA> codeList ->
            if (CollectionUtils.isEmpty(codeList)) {
                String code = codeGenerator.generateCode()
                if (userTFA.verifyType == TFAVerifyType.CALL.toString()) {
                    if (code.length() > 5) {
                        // TFA CALL will have the limit to 100 - 99999 limitation
                        userTFA.verifyCode = code.substring(0, 5)
                    }
                } else {
                    userTFA.verifyCode = code
                }
            } else {
                userTFA.verifyCode = codeList.get(0).verifyCode
            }

            return Promise.pure(null)
        }
    }

    private Long getTimeStartOffset(Integer offset) {
        Calendar calendar = Calendar.instance
        calendar.setTime(new Date())
        calendar.add(Calendar.SECOND, -offset)
        return calendar.getTime().getTime()
    }

    private Promise<Void> basicTFACheck(UserId userId, UserTFA userTFA) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTFA == null) {
            throw new IllegalArgumentException('userTFA is null')
        }

        if (userTFA.userId != null && userTFA.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userTFA.userId, userId).exception()
        }

        if (userTFA.verifyType == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('verifyType').exception()
        }

        List<String> allowedVerifyTypes = TFAVerifyType.values().collect { TFAVerifyType verifyType ->
            verifyType.toString()
        }
        if (!(userTFA.verifyType in allowedVerifyTypes)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('verifyType', allowedVerifyTypes.join(',')).exception()
        }

        if (userTFA.personalInfo == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('personalInfo').exception()
        }

        if (userTFA.verifyType == TFAVerifyType.MAIL.toString()) {
            if (!StringUtils.isEmpty(userTFA.template)) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('template').exception()
            }
        } else {
            if (userTFA.template != null) {
                if (userTFA.template.length() > maxTemplateLength) {
                    throw AppCommonErrors.INSTANCE.fieldTooLong('template', maxTemplateLength).exception()
                }
                if (userTFA.template.length() < minTemplateLength) {
                    throw AppCommonErrors.INSTANCE.fieldTooShort('template', minTemplateLength).exception()
                }
            }
        }

        return validatePersonalInfo(userId, userTFA.personalInfo, userTFA.verifyType, userTFA).then {
            return validateLocale(userTFA)
        }
    }

    private Promise<Void> validateLocale(UserTFA userTFA) {
        if (userTFA.sentLocale == null) {
            return Promise.pure(null)
        }

        return localeRepository.get(userTFA.sentLocale).then { com.junbo.identity.spec.v1.model.Locale locale ->
            if (locale == null) {
                throw AppErrors.INSTANCE.localeNotFound(userTFA.sentLocale).exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> validatePersonalInfo(UserId userId, UserPersonalInfoId personalInfoId, String verifyType, UserTFA userTFA) {
        if (verifyType == TFAVerifyType.MAIL.toString()) {
            return validateEmail(userId, personalInfoId, userTFA).then {
                return userPersonalInfoRepository.get(personalInfoId).then { UserPersonalInfo personalInfo ->
                    Email email = (Email)JsonHelper.jsonNodeToObj(personalInfo.value, Email)
                    userTFA.email = email.info

                    return Promise.pure(null)
                }
            }
        } else {
            return validatePhoneNumber(userId, personalInfoId, userTFA)
        }
    }

    private Promise<Void> validateEmail(UserId userId, UserPersonalInfoId mail, UserTFA userTFA) {
        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            userTFA.username = user.username

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (user.emails == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('personalInfo', 'user has no mails').exception()
            }

            return validateUserMailList(user.emails.iterator(), mail).then { Boolean exists ->
                if (!exists) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('personalInfo', 'personalInfo isn\'t user user.').exception()
                }

                return Promise.pure(null)
            }
        }
    }

    private Promise<Boolean> validateUserMailList(Iterator<UserPersonalInfoLink> iterator,
                                                  UserPersonalInfoId mail) {
        if (iterator.hasNext()) {
            UserPersonalInfoLink link = (UserPersonalInfoLink)iterator.next()

            if (link.value == mail) {
                return Promise.pure(true)
            } else {
                return validateUserMailList(iterator, mail)
            }
        }

        return Promise.pure(false)
    }

    private Promise<Void> validatePhoneNumber(UserId userId, UserPersonalInfoId phoneNumber, UserTFA userTFA) {
        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            userTFA.username = user.username

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (user.phones == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('personalInfo', 'user has no phones').exception()
            }

            return validateUserPhoneLinkList(user.phones.iterator(), phoneNumber).then { Boolean exists ->
                if (!exists) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('personalInfo',
                            'personalInfo isn\'t under user.').exception()
                }

                return Promise.pure(null)
            }
        }
    }

    private Promise<Boolean> validateUserPhoneLinkList(Iterator<UserPersonalInfoLink> iterator,
                                                       UserPersonalInfoId phoneNumber) {
        if (iterator.hasNext()) {
            UserPersonalInfoLink link = (UserPersonalInfoLink)iterator.next()

            if (link.value == phoneNumber) {
                return Promise.pure(true)
            } else {
                return validateUserPhoneLinkList(iterator, phoneNumber)
            }
        }

        return Promise.pure(false)
    }

    @Required
    void setMaxSMSRequestsPerHour(Integer maxSMSRequestsPerHour) {
        this.maxSMSRequestsPerHour = maxSMSRequestsPerHour
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setMaxTFACodeExpireTime(Integer maxTFACodeExpireTime) {
        this.maxTFACodeExpireTime = maxTFACodeExpireTime
    }

    @Required
    void setUserTFAPhoneRepository(UserTFAPhoneRepository userTFAPhoneRepository) {
        this.userTFAPhoneRepository = userTFAPhoneRepository
    }

    @Required
    void setUserTFAMailRepository(UserTFAMailRepository userTFAMailRepository) {
        this.userTFAMailRepository = userTFAMailRepository
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Required
    void setLocaleRepository(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository
    }

    @Required
    void setMinTemplateLength(Integer minTemplateLength) {
        this.minTemplateLength = minTemplateLength
    }

    @Required
    void setMaxTemplateLength(Integer maxTemplateLength) {
        this.maxTemplateLength = maxTemplateLength
    }

    @Required
    void setMaxReuseSeconds(Integer maxReuseSeconds) {
        this.maxReuseSeconds = maxReuseSeconds
    }

    @Required
    void setCodeGenerator(CodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator
    }
}
