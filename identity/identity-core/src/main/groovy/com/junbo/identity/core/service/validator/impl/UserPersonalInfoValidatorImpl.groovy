package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.core.service.validator.PiiValidatorFactory
import com.junbo.identity.core.service.validator.UserPersonalInfoValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Checks is here:  https://oculus.atlassian.net/wiki/display/SER/Resource%3A+userPersonalInfo
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class UserPersonalInfoValidatorImpl implements UserPersonalInfoValidator {

    /*
    * The userPersonalInfo validation: https://oculus.atlassian.net/wiki/display/SER/Resource%3A+userPersonalInfo
    * */

    private UserPersonalInfoRepository userPersonalInfoRepository
    private UserRepository userRepository
    private OrganizationRepository organizationRepository

    private PiiValidatorFactory piiValidatorFactory

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
        if (options.userId == null && options.email == null && options.phoneNumber == null && options.name == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId or email or phoneNumber or name').exception()
        }

        if (options.userId != null && (options.email != null || options.phoneNumber != null || options.name != null)) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('userId and (email or phone or name)', 'userId can\'t be searched with email or phone or name').exception()
        }

        if (options.isValidated != null && options.userId == null) {
        //    throw AppCommonErrors.INSTANCE.parameterInvalid('isValidated can be searched by userId only').exception()
        }
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }

        if (userPersonalInfo.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
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
            throw AppCommonErrors.INSTANCE.fieldInvalid('id', oldUserPersonalInfo.id.toString()).exception()
        }

        if (userPersonalInfo.type != oldUserPersonalInfo.type) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('type', 'type can\'t be updated.').exception()
        }

        if (userPersonalInfo.id == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
        }

        return checkBasicPersonalInfo(userPersonalInfo).then {
            return checkAdvancedUpdate(userPersonalInfo, oldUserPersonalInfo)
        }
    }

    Promise<Void> checkBasicPersonalInfo(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }
        if (userPersonalInfo.userId == null && userPersonalInfo.organizationId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId or organizationId').exception()
        }

        if (userPersonalInfo.userId != null && userPersonalInfo.organizationId != null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('userId or organizationId',
                    'userId and organizationId can\'t appear both.').exception()
        }

        if (userPersonalInfo.value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value').exception()
        }
        if (userPersonalInfo.type == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        }
        List<String> allowedValues = UserPersonalInfoType.values().collect { UserPersonalInfoType type ->
            type.toString()
        }
        if (!(userPersonalInfo.type in allowedValues)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('type', allowedValues.join(',')).exception()
        }

        if (userPersonalInfo.lastValidateTime != null) {
            if (!isValidTimeScope(userPersonalInfo.lastValidateTime)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('lastValidateTime').exception()
            }
        }

        return Promise.pure(null)
    }

    Promise<Void> checkAdvancedCreate(UserPersonalInfo userPersonalInfo) {
        List<PiiValidator> piiValidatorList = piiValidatorFactory.validators
        return iterateValidateCreate(piiValidatorList.iterator(), userPersonalInfo).then {
            return checkPiiOwnerShipExists(userPersonalInfo)
        }
    }

    Promise<Void> iterateValidateCreate(Iterator<PiiValidator> iterator, UserPersonalInfo userPersonalInfo) {
        if (iterator.hasNext()) {
            PiiValidator piiValidator = iterator.next()
            if (piiValidator.handles(userPersonalInfo.type)) {
                userPersonalInfo.value = piiValidator.updateJsonNode(userPersonalInfo.value)
                return piiValidator.validateCreate(userPersonalInfo.value, userPersonalInfo.userId, userPersonalInfo.organizationId).then {
                    return iterateValidateCreate(iterator, userPersonalInfo)
                }
            }
            return iterateValidateCreate(iterator, userPersonalInfo)
        }
        return Promise.pure(null)
    }

    Promise<Void> checkAdvancedUpdate(UserPersonalInfo userPersonalInfo, UserPersonalInfo oldUserPersonalInfo) {
        List<PiiValidator> piiValidatorList = piiValidatorFactory.validators
        return iterateValidateUpdate(piiValidatorList.iterator(), userPersonalInfo, oldUserPersonalInfo).then {
            return checkPiiOwnerShipExists(userPersonalInfo)
        }
    }

    Promise<Void> iterateValidateUpdate(Iterator<PiiValidator> iterator, UserPersonalInfo userPersonalInfo,
                                        UserPersonalInfo oldUserPersonalInfo) {
        if (iterator.hasNext()) {
            PiiValidator piiValidator = iterator.next()
            if (piiValidator.handles(userPersonalInfo.type)) {
                userPersonalInfo.value = piiValidator.updateJsonNode(userPersonalInfo.value)
                return piiValidator.validateUpdate(userPersonalInfo.value, oldUserPersonalInfo.value).then {
                    return iterateValidateUpdate(iterator, userPersonalInfo, oldUserPersonalInfo)
                }
            }
            return iterateValidateUpdate(iterator, userPersonalInfo, oldUserPersonalInfo)
        }
        return Promise.pure(null)
    }

    Promise<Void> checkPiiOwnerShipExists(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo.userId != null) {
            return userRepository.get(userPersonalInfo.userId).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userPersonalInfo.userId).exception()
                }

                if (user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userPersonalInfo.userId).exception()
                }

                return Promise.pure(null)
            }
        } else if (userPersonalInfo.organizationId) {
            return organizationRepository.get(userPersonalInfo.organizationId).then { Organization organization ->
                if (organization == null) {
                    throw AppErrors.INSTANCE.organizationNotFound(userPersonalInfo.organizationId).exception()
                }

                return Promise.pure(null)
            }
        } else {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId or organizationId').exception()
        }
    }

    private Boolean isValidTimeScope(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        return date.before(cal.getTime());
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository
    }

    @Required
    void setPiiValidatorFactory(PiiValidatorFactory piiValidatorFactory) {
        this.piiValidatorFactory = piiValidatorFactory
    }
}
