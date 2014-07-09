package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.core.service.util.CodeGenerator
import com.junbo.identity.core.service.validator.UserTFAValidator
import com.junbo.identity.data.identifiable.TFAVerifyType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTFARepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.identity.spec.v1.option.list.UserTFAListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class UserTFAValidatorImpl implements UserTFAValidator {
    private Integer maxTFACodeExpireTime
    private Integer maxSMSRequestsPerHour
    private Integer maxReuseSeconds

    private CodeGenerator codeGenerator

    private UserRepository userRepository
    private UserTFARepository userTFARepository
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

            return userTFARepository.get(userTFAId).then { UserTFA existingUserTFACode ->
                if (existingUserTFACode == null) {
                    throw AppErrors.INSTANCE.userTFANotFound(userTFAId).exception()
                }

                if (existingUserTFACode.userId != userId) {
                    throw AppCommonErrors.INSTANCE.parameterInvalid('userTFACodeId and userId doesn\'t match.').exception()
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

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTFA userTeleCode) {

        if (userTeleCode.verifyCode != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('verifyCode').exception()
        }

        return basicTFACheck(userId, userTeleCode).then {
            return fillCode(userId, userTeleCode)
        }.then {
                if (userTeleCode.id != null) {
                    throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
                }

                if (userTeleCode.expiresBy != null) {
                    throw AppCommonErrors.INSTANCE.fieldMustBeNull('expiresBy').exception()
                }

                if (userTeleCode.active != null) {
                    throw AppCommonErrors.INSTANCE.fieldMustBeNull('active').exception()
                }

                Calendar cal = Calendar.instance
                cal.setTime(new Date())
                cal.add(Calendar.SECOND, maxTFACodeExpireTime)
                userTeleCode.expiresBy = cal.time

                userTeleCode.active = true
                userTeleCode.userId = userId

                return Promise.pure(null)
            }.then {
                return userTFARepository.searchTFACodeByUserIdAndPersonalInfoId(userId, userTeleCode.personalInfo,
                        Integer.MAX_VALUE, 0).then { List<UserTFA> userTeleCodeList ->
                            if (CollectionUtils.isEmpty(userTeleCodeList)
                                    || userTeleCodeList.size() <= maxSMSRequestsPerHour) {
                                return Promise.pure(null)
                            }

                            userTeleCodeList.sort(new Comparator<UserTFA> () {
                                @Override
                                int compare(UserTFA o1, UserTFA o2) {
                                    Date o1LastChangedTime = o1.updatedTime == null ? o1.createdTime : o1.updatedTime
                                    Date o2LastChangedTime = o2.updatedTime == null ? o2.createdTime : o2.updatedTime

                                    return o2LastChangedTime <=> o1LastChangedTime
                                }
                            }
                            )

                            UserTFA teleCode = userTeleCodeList.get(maxSMSRequestsPerHour - 1)
                            Date lastChangeTime = teleCode.updatedTime == null ? teleCode.createdTime : teleCode.updatedTime

                            Calendar cal = Calendar.instance
                            cal.setTime(new Date())
                            cal.add(Calendar.HOUR, -1)
                            if (lastChangeTime.after(cal.time)) {
                                throw AppCommonErrors.INSTANCE.fieldInvalid('userId',
                                        'Reach maximum request number per hour').exception()
                            }

                            return Promise.pure(null)
                        }
            }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTFAId userTFAId, UserTFA userTFA, UserTFA oldUserTFA) {

        if (userTFA.verifyCode != null && userTFA.verifyCode != oldUserTFA.verifyCode) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('verifyCode').exception()
        }

        return basicTFACheck(userId, userTFA).then {
            return userTFARepository.get(userTFAId).then { UserTFA tfa ->
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

    private Promise<Void> fillCode(UserId userId, UserTFA userTFA) {
        return userTFARepository.searchTFACodeByUserIdAndPersonalInfoId(userId, userTFA.personalInfo,
                Integer.MAX_VALUE, 0).then { List<UserTFA> codeList ->
            if (CollectionUtils.isEmpty(codeList)) {
                userTFA.verifyCode = codeGenerator.generateCode()
            }

            UserTFA tfa = codeList.find { UserTFA code ->
                Calendar calendar = Calendar.instance
                calendar.setTime(new Date())
                calendar.add(Calendar.SECOND, -maxReuseSeconds)
                Date date = code.updatedTime != null ? code.updatedTime : code.createdTime
                return date.after(calendar.time)
            }

            if (tfa != null) {
                userTFA.verifyCode = tfa.verifyCode
            } else {
                userTFA.verifyCode = codeGenerator.generateCode()
            }

            return Promise.pure(null)
        }
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

        if (userTFA.template != null) {
            if (userTFA.template.length() > maxTemplateLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('template', maxTemplateLength).exception()
            }
            if (userTFA.template.length() < minTemplateLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('template', minTemplateLength).exception()
            }
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

        return validatePhoneNumber(userId, userTFA.personalInfo).then {
            return validateLocale(userTFA).then {
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

                    return Promise.pure(null)
                }
            }
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

    private Promise<Void> validatePhoneNumber(UserId userId, UserPersonalInfoId phoneNumber) {
        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (user.isAnonymous == true) {
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
    void setUserTFARepository(UserTFARepository userTFARepository) {
        this.userTFARepository = userTFARepository
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
