package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.core.service.validator.PiiValidatorFactory
import com.junbo.identity.core.service.validator.UserPersonalInfoValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class UserPersonalInfoValidatorImpl implements UserPersonalInfoValidator {

    private UserPersonalInfoRepository userPersonalInfoRepository
    private UserRepository userRepository

    private PiiValidatorFactory piiValidatorFactory

    private Integer minLabelLength
    private Integer maxLabelLength

    @Override
    Promise<UserPersonalInfo> validateForGet(UserPersonalInfoId userPersonalInfoId) {
        if (userPersonalInfoId == null) {
            throw new IllegalArgumentException('userPersonalInfoId is null')
        }

        return userPersonalInfoRepository.get(userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoId).exception()
            }

            return Promise.pure(userPersonalInfo)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserPersonalInfoListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }
        if (options.userId == null && options.email == null && options.phoneNumber == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId or email or phoneNumber').exception()
        }

        if (options.userId != null && (options.email != null || options.phoneNumber != null)) {
            throw AppErrors.INSTANCE.parameterInvalid('userId can\'t be searched with email or phone.').exception()
        }

        if (options.email != null && options.phoneNumber != null) {
            throw AppErrors.INSTANCE.parameterInvalid('email can\'t be searched with phone.').exception()
        }
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }

        if (userPersonalInfo.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        return checkBasicPersonalInfo(userPersonalInfo).then {
            return checkAdvancedCreate(userPersonalInfo)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserPersonalInfo userPersonalInfo, UserPersonalInfo oldUserPersonalInfo) {
        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }

        if (userPersonalInfo.id != oldUserPersonalInfo.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id', oldUserPersonalInfo.id.toString()).exception()
        }

        if (userPersonalInfo.id == null) {
            throw AppErrors.INSTANCE.fieldRequired('id').exception()
        }
        return checkBasicPersonalInfo(userPersonalInfo).then {
            return checkAdvancedUpdate(userPersonalInfo, oldUserPersonalInfo)
        }
    }

    Promise<Void> checkBasicPersonalInfo(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }
        if (userPersonalInfo.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        if (userPersonalInfo.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
        if (userPersonalInfo.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }
        List<String> allowedValues = UserPersonalInfoType.values().collect { UserPersonalInfoType type ->
            type.toString()
        }
        if (!(userPersonalInfo.type in allowedValues)) {
            throw AppErrors.INSTANCE.fieldInvalid('type', allowedValues.join(',')).exception()
        }

        if (userPersonalInfo.label != null) {
            if (userPersonalInfo.label.length() > maxLabelLength) {
                throw AppErrors.INSTANCE.fieldTooLong('label', maxLabelLength).exception()
            }
            if (userPersonalInfo.label.length() < minLabelLength) {
                throw AppErrors.INSTANCE.fieldTooShort('label', minLabelLength).exception()
            }
        }
        if (userPersonalInfo.lastValidateTime != null) {
            if (userPersonalInfo.lastValidateTime.after(new Date())) {
                throw AppErrors.INSTANCE.fieldInvalid('lastValidateTime').exception()
            }
        }

        return Promise.pure(null)
    }

    Promise<Void> checkAdvancedCreate(UserPersonalInfo userPersonalInfo) {
        List<PiiValidator> piiValidatorList = piiValidatorFactory.validators
        return iterateValidateCreate(piiValidatorList.iterator(), userPersonalInfo).then {
            return userRepository.get(userPersonalInfo.userId).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userPersonalInfo.userId).exception()
                }

                if (user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userPersonalInfo.userId).exception()
                }

                return Promise.pure(null)
            }
        }
    }

    Promise<Void> checkAdvancedUpdate(UserPersonalInfo userPersonalInfo, UserPersonalInfo oldUserPersonalInfo) {
        List<PiiValidator> piiValidatorList = piiValidatorFactory.validators
        return iterateValidateUpdate(piiValidatorList.iterator(), userPersonalInfo, oldUserPersonalInfo).then {
            return userRepository.get(userPersonalInfo.userId).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userPersonalInfo.userId).exception()
                }

                if (user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userPersonalInfo.userId).exception()
                }

                return Promise.pure(null)
            }
        }
    }

    Promise<Void> iterateValidateCreate(Iterator<PiiValidator> iterator, UserPersonalInfo userPersonalInfo) {
        if (iterator.hasNext()) {
            PiiValidator piiValidator = iterator.next()
            if (piiValidator.handles(userPersonalInfo.type)) {
                return piiValidator.validateCreate(userPersonalInfo.value, userPersonalInfo.userId).then {
                    return iterateValidateCreate(iterator, userPersonalInfo)
                }
            }
            return iterateValidateCreate(iterator, userPersonalInfo)
        }
        return Promise.pure(null)
    }

    Promise<Void> iterateValidateUpdate(Iterator<PiiValidator> iterator, UserPersonalInfo userPersonalInfo,
                                        UserPersonalInfo oldUserPersonalInfo) {
        if (iterator.hasNext()) {
            PiiValidator piiValidator = iterator.next()
            if (piiValidator.handles(userPersonalInfo.type)) {
                return piiValidator.validateUpdate(userPersonalInfo.value, oldUserPersonalInfo.value,
                        userPersonalInfo.userId).then {
                    return iterateValidateUpdate(iterator, userPersonalInfo, oldUserPersonalInfo)
                }
            }
            return iterateValidateUpdate(iterator, userPersonalInfo, oldUserPersonalInfo)
        }
        return Promise.pure(null)
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Required
    void setMinLabelLength(Integer minLabelLength) {
        this.minLabelLength = minLabelLength
    }

    @Required
    void setMaxLabelLength(Integer maxLabelLength) {
        this.maxLabelLength = maxLabelLength
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setPiiValidatorFactory(PiiValidatorFactory piiValidatorFactory) {
        this.piiValidatorFactory = piiValidatorFactory
    }
}
