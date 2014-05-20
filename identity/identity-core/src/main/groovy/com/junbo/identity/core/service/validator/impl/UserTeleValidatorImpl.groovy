package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTeleId
import com.junbo.identity.core.service.util.CodeGenerator
import com.junbo.identity.core.service.validator.UserTeleValidator
import com.junbo.identity.data.identifiable.TeleVerifyType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTeleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.identity.spec.v1.option.list.UserTeleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class UserTeleValidatorImpl implements UserTeleValidator {
    private Integer maxTeleCodeExpireTime
    private Integer maxSMSRequestsPerHour
    private Integer maxReuseSeconds

    private CodeGenerator codeGenerator

    private UserRepository userRepository
    private UserTeleRepository userTeleRepository
    private UserPersonalInfoRepository userPersonalInfoRepository
    private LocaleRepository localeRepository

    private Integer minTemplateLength
    private Integer maxTemplateLength

    @Override
    Promise<UserTeleCode> validateForGet(UserId userId, UserTeleId userTeleId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTeleId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userTeleId').exception()
        }

        return userRepository.get(userId).then { User existing ->
            if (existing == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (existing.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (existing.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTeleRepository.get(userTeleId).then { UserTeleCode existingUserTeleCode ->
                if (existingUserTeleCode == null) {
                    throw AppErrors.INSTANCE.userTeleCodeNotFound(userTeleId).exception()
                }

                if (existingUserTeleCode.userId != userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userTeleCodeId and userId doesn\'t match.').exception()
                }

                return Promise.pure(existingUserTeleCode)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserTeleListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (options.phoneNumber == null) {
            throw AppErrors.INSTANCE.parameterRequired('phoneNumber').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTeleCode userTeleCode) {

        if (userTeleCode.verifyCode != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('verifyCode').exception()
        }

        return basicTeleCodeCheck(userId, userTeleCode).then {
            return fillCode(userId, userTeleCode)
        }.then {
                if (userTeleCode.id != null) {
                    throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
                }

                if (userTeleCode.expiresBy != null) {
                    throw AppErrors.INSTANCE.fieldNotWritable('expiresBy').exception()
                }

                if (userTeleCode.active != null) {
                    throw AppErrors.INSTANCE.fieldNotWritable('active').exception()
                }

                Calendar cal = Calendar.instance
                cal.setTime(new Date())
                cal.add(Calendar.SECOND, maxTeleCodeExpireTime)
                userTeleCode.expiresBy = cal.time

                userTeleCode.active = true
                userTeleCode.userId = userId

                return Promise.pure(null)
            }.then {
                return userTeleRepository.searchTeleCode(userId, userTeleCode.phoneNumber).
                        then { List<UserTeleCode> userTeleCodeList ->
                            if (CollectionUtils.isEmpty(userTeleCodeList)
                                    || userTeleCodeList.size() <= maxSMSRequestsPerHour) {
                                return Promise.pure(null)
                            }

                            userTeleCodeList.sort(new Comparator<UserTeleCode> () {
                                @Override
                                int compare(UserTeleCode o1, UserTeleCode o2) {
                                    Date o1LastChangedTime = o1.updatedTime == null ? o1.createdTime : o1.updatedTime
                                    Date o2LastChangedTime = o2.updatedTime == null ? o2.createdTime : o2.updatedTime

                                    return o2LastChangedTime <=> o1LastChangedTime
                                }
                            }
                            )

                            UserTeleCode teleCode = userTeleCodeList.get(maxSMSRequestsPerHour - 1)
                            Date lastChangeTime = teleCode.updatedTime == null ? teleCode.createdTime : teleCode.updatedTime

                            Calendar cal = Calendar.instance
                            cal.setTime(new Date())
                            cal.add(Calendar.HOUR, -1)
                            if (lastChangeTime.after(cal.time)) {
                                throw AppErrors.INSTANCE.fieldInvalidException('userId',
                                        'Reach maximum request number per hour').exception()
                            }

                            return Promise.pure(null)
                        }
            }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTeleId userTeleId, UserTeleCode userTeleCode,
                                    UserTeleCode oldUserTeleCode) {

        if (userTeleCode.verifyCode != null && userTeleCode.verifyCode != oldUserTeleCode.verifyCode) {
            throw AppErrors.INSTANCE.fieldNotWritable('verifyCode').exception()
        }

        return basicTeleCodeCheck(userId, userTeleCode).then {
            return userTeleRepository.get(userTeleId).then { UserTeleCode teleCode ->
                if (teleCode == null) {
                    throw AppErrors.INSTANCE.userTeleCodeNotFound(userTeleId).exception()
                }

                return Promise.pure(null)
            }
        }.then {
            if (userTeleCode.id == null) {
                throw AppErrors.INSTANCE.fieldRequired('id').exception()
            }

            if (userTeleCode.expiresBy == null) {
                throw AppErrors.INSTANCE.fieldRequired('expiresBy').exception()
            }

            if (userTeleCode.active == null) {
                throw AppErrors.INSTANCE.fieldRequired('active').exception()
            }

            // TeleCode can't update teleCode number & verifyCode & userId
            if (userTeleCode.phoneNumber != oldUserTeleCode.phoneNumber) {
                throw AppErrors.INSTANCE.fieldInvalid('phoneNumber').exception()
            }

            if (userTeleCode.userId != userId) {
                throw AppErrors.INSTANCE.fieldInvalid('userId', userId.toString()).exception()
            }

            if (userTeleCode.userId != oldUserTeleCode.userId) {
                throw AppErrors.INSTANCE.fieldInvalid('userId', oldUserTeleCode.userId.toString()).exception()
            }

            if (userTeleCode.verifyCode != oldUserTeleCode.verifyCode) {
                throw AppErrors.INSTANCE.fieldInvalid('verifyCode', oldUserTeleCode.verifyCode).exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> fillCode(UserId userId, UserTeleCode userTeleCode) {
        return userTeleRepository.searchTeleCode(userId, userTeleCode.phoneNumber).then { List<UserTeleCode> codeList ->
            if (CollectionUtils.isEmpty(codeList)) {
                userTeleCode.verifyCode = codeGenerator.generateTeleCode()
            }

            UserTeleCode teleCode = codeList.find { UserTeleCode code ->
                Calendar calendar = Calendar.instance
                calendar.setTime(new Date())
                calendar.add(Calendar.SECOND, -maxReuseSeconds)
                Date date = code.updatedTime != null ? code.updatedTime : code.createdTime
                return date.after(calendar.time)
            }

            if (teleCode != null) {
                userTeleCode.verifyCode = teleCode.verifyCode
            } else {
                userTeleCode.verifyCode = codeGenerator.generateTeleCode()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> basicTeleCodeCheck(UserId userId, UserTeleCode userTeleCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleCode == null) {
            throw new IllegalArgumentException('userTeleCode is null')
        }

        if (userTeleCode.userId != null && userTeleCode.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userId.toString()).exception()
        }

        if (userTeleCode.template != null) {
            if (userTeleCode.template.length() > maxTemplateLength) {
                throw AppErrors.INSTANCE.fieldTooLong('template', maxTemplateLength).exception()
            }
            if (userTeleCode.template.length() < minTemplateLength) {
                throw AppErrors.INSTANCE.fieldTooShort('template', minTemplateLength).exception()
            }
        }

        if (userTeleCode.verifyType == null) {
            throw AppErrors.INSTANCE.fieldRequired('verifyType').exception()
        }

        List<String> allowedVerifyTypes = TeleVerifyType.values().collect { TeleVerifyType teleVerifyType ->
            teleVerifyType.toString()
        }
        if (!(userTeleCode.verifyType in allowedVerifyTypes)) {
            throw AppErrors.INSTANCE.fieldInvalid('verifyType', allowedVerifyTypes.join(',')).exception()
        }

        if (userTeleCode.phoneNumber == null) {
            throw AppErrors.INSTANCE.fieldRequired('phoneNumber').exception()
        }

        return validatePhoneNumber(userId, userTeleCode.phoneNumber).then {
            return validateLocale(userTeleCode).then {
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

    private Promise<Void> validateLocale(UserTeleCode userTeleCode) {
        if (userTeleCode.sentLocale == null) {
            return Promise.pure(null)
        }

        return localeRepository.get(userTeleCode.sentLocale).then { com.junbo.identity.spec.v1.model.Locale locale ->
            if (locale == null) {
                throw AppErrors.INSTANCE.localeNotFound(userTeleCode.sentLocale).exception()
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
                throw AppErrors.INSTANCE.fieldInvalidException('phoneNumber', 'user has no phones').exception()
            }

            return validateUserPhoneLinkList(user.phones.iterator(), phoneNumber).then { Boolean exists ->
                if (!exists) {
                    throw AppErrors.INSTANCE.fieldInvalidException('phoneNumber',
                            'phoneNumber isn\'t under user.').exception()
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
    void setMaxTeleCodeExpireTime(Integer maxTeleCodeExpireTime) {
        this.maxTeleCodeExpireTime = maxTeleCodeExpireTime
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
    void setUserTeleRepository(UserTeleRepository userTeleRepository) {
        this.userTeleRepository = userTeleRepository
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
