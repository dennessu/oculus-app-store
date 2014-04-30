package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.validator.UserTeleValidator
import com.junbo.identity.data.identifiable.TeleVerifyType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTeleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.identity.spec.v1.option.list.UserTeleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class UserTeleValidatorImpl implements UserTeleValidator {
    private Integer maxTeleCodeExpireTime
    private Integer maxSMSRequestsPerHour

    private UserRepository userRepository
    private UserTeleRepository userTeleRepository
    private UserPersonalInfoRepository userPersonalInfoRepository

    private List<String> allowedLanguages
    private Integer minVerifyCodeLength
    private Integer maxVerifyCodeLength
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

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTeleCode userTeleCode) {
        return basicTeleCodeCheck(userId, userTeleCode)

        // userTeleRepository.searchActiveTeleCode(userId, userTeleCode.phoneNumber)
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTeleId userTeleId, UserTeleCode userTeleCode,
                                    UserTeleCode oldUserTeleCode) {
        return null
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

        if (userTeleCode.sentLanguage != null) {
            if (!(userTeleCode.sentLanguage in allowedLanguages)) {
                throw AppErrors.INSTANCE.fieldInvalid('sentLanguage', allowedLanguages.join(',')).exception()
            }
        }

        if (userTeleCode.verifyCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('verifyCode').exception()
        }
        if (userTeleCode.verifyCode.length() > maxVerifyCodeLength) {
            throw AppErrors.INSTANCE.fieldTooLong('verifyCode', maxVerifyCodeLength).exception()
        }
        if (userTeleCode.verifyCode.length() < minVerifyCodeLength) {
            throw AppErrors.INSTANCE.fieldTooShort('verifyCode', minVerifyCodeLength).exception()
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

    private Promise<Void> validatePhoneNumber(UserId userId, String phoneNumber) {
        userRepository.get(userId).then { User user ->
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

    private Promise<Boolean> validateUserPhoneLinkList(Iterator<UserPersonalInfoLink> iterator, String phoneNumber) {
        if (iterator.hasNext()) {
            UserPersonalInfoLink link = (UserPersonalInfoLink)iterator.next()

            return validateUserPhoneLink(link, phoneNumber).then { Boolean isValid ->
                if (isValid == true) {
                    return Promise.pure(true)
                }
                return validateUserPhoneLinkList(iterator, phoneNumber)
            }
        }

        return Promise.pure(false)
    }

    private Promise<Boolean> validateUserPhoneLink(UserPersonalInfoLink link, String phoneNumber) {
        return userPersonalInfoRepository.get(link.value).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(link.value).exception()
            }

            PhoneNumber userPhone = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.value, PhoneNumber)
            if (userPhone.value == phoneNumber) {
                return Promise.pure(true)
            }
            return Promise.pure(false)
        }
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
}
