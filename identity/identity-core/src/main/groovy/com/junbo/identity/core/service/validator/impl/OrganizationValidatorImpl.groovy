package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.normalize.NormalizeService
import com.junbo.identity.core.service.validator.OrganizationValidator
import com.junbo.identity.data.identifiable.OrganizationTaxType
import com.junbo.identity.data.identifiable.OrganizationType
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Check owner exists
 * Created by liangfu on 5/22/14.
 */
@CompileStatic
class OrganizationValidatorImpl implements OrganizationValidator {

    private OrganizationRepository organizationRepository
    private UserRepository userRepository
    private UserPersonalInfoRepository userPersonalInfoRepository
    private NormalizeService normalizeService

    @Override
    Promise<Organization> validateForGet(OrganizationId organizationId) {
        if (organizationId == null) {
            throw new IllegalArgumentException('organizationId is null')
        }
        return organizationRepository.get(organizationId).then { Organization organization ->
            if (organization == null) {
                throw AppErrors.INSTANCE.organizationNotFound(organizationId).exception()
            }

            return Promise.pure(organization)
        }
    }

    @Override
    Promise<Void> validateForSearch(OrganizationListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.ownerId == null && StringUtils.isEmpty(options.name)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('owner or name').exception()
        }

        if (options.ownerId != null && !StringUtils.isEmpty(options.name)) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('owner and name can\'t be exist at the same time').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Organization organization) {
        if (organization.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        return checkBasicOrganizationInfo(organization).then{
            if (organization.isValidated) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('isValidated', 'Can\'t create validated organization').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(OrganizationId organizationId, Organization organization,
                                    Organization oldOrganization) {
        if (organization.id == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
        }
        if (organizationId != organization.id) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('id', organization.id, organizationId.toString()).exception()
        }
        if (organization.id != oldOrganization.id) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('id', organization.id, oldOrganization.id.toString()).exception()
        }

        return checkBasicOrganizationInfo(organization)
    }

    Promise<Void> checkBasicOrganizationInfo(Organization organization) {
        if (organization == null) {
            throw new IllegalArgumentException('organization is null')
        }

        if (organization.isValidated == null) {
            throw new IllegalArgumentException('isValidated is null')
        }

        if (organization.type != null) {
            if (!OrganizationType.values().any { OrganizationType organizationType ->
                return organizationType.toString() == organization.type
            }) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('type').exception()
            }
        }
        organization.canonicalName = normalizeService.normalize(organization.name)

        if (organization.publisherRevenueRatio != null) {
            if (organization.publisherRevenueRatio < 0.0 || organization.publisherRevenueRatio > 1) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('publisherRevenueRatio', 'publisherRevenueRatio should be between 0 and 1').exception()
            }
        }

        return checkValidOrganizationNameUnique(organization).then {
            if (organization.billingAddress == null) {
                return Promise.pure(null)
            }
            return checkPersonalInfoIdOwner(organization.billingAddress, organization.ownerId, UserPersonalInfoType.ADDRESS.toString())
        }.then {
            if (organization.shippingAddress == null) {
                return Promise.pure(null)
            }
            return checkPersonalInfoIdOwner(organization.shippingAddress, organization.ownerId, UserPersonalInfoType.ADDRESS.toString())
        }.then {
            if (organization.shippingName == null) {
                return Promise.pure(null)
            }
            return checkPersonalInfoIdOwner(organization.shippingName, organization.ownerId, UserPersonalInfoType.NAME.toString())
        }.then {
            if (organization.shippingPhone == null) {
                return Promise.pure(null)
            }
            return checkPersonalInfoIdOwner(organization.shippingPhone, organization.ownerId, UserPersonalInfoType.PHONE.toString())
        }.then {
            if (organization.taxId == null) {
                return Promise.pure(null)
            }
            List<String> allowedValues = OrganizationTaxType.values().collect { OrganizationTaxType type ->
                return type.toString()
            }
            return checkPersonalInfoIdOwner(organization.taxId, organization.ownerId, null).then { UserPersonalInfo userPersonalInfo ->
                if (!(userPersonalInfo.type in allowedValues)) {
                    throw AppCommonErrors.INSTANCE.fieldInvalidEnum('taxId.type', allowedValues.join(',')).exception()
                }

                return Promise.pure(null)
            }
        }.then {
            if (organization.ownerId == null) {
                return Promise.pure(null)
            }
            return userRepository.get(organization.ownerId).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(organization.ownerId).exception()
                }

                if (user.isAnonymous == null || user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(organization.ownerId).exception()
                }

                return Promise.pure(null)
            }
        }
    }

    private Promise<Void> checkValidOrganizationNameUnique(Organization organization) {

        return organizationRepository.searchByCanonicalName(organization.canonicalName, Integer.MAX_VALUE, 0).then { Results<Organization> results ->
            if (results == null || CollectionUtils.isEmpty(results.items)) {
                return Promise.pure(null)
            }

            def organizationList = results.items

            organizationList.removeAll { Organization org ->
                return !org.isValidated || org.id == organization.id
            }

            if (!CollectionUtils.isEmpty(organizationList)) {
                throw AppCommonErrors.INSTANCE.fieldDuplicate('name').exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<UserPersonalInfo> checkPersonalInfoIdOwner(UserPersonalInfoId userPersonalInfoId, UserId ownerId, String expectedType) {
        return userPersonalInfoRepository.get(userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoId).exception()
            }

            if (!StringUtils.isEmpty(expectedType) && userPersonalInfo.type != expectedType) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('userPersonalInfoId', 'Type isn\'t valid, expected: ' + expectedType).exception()
            }

            if (userPersonalInfo.userId != ownerId) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('userPersonalInfoId', 'UserId isn\'t match with ownerId').exception()
            }

            return Promise.pure(userPersonalInfo)
        }
    }

    @Required
    void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Required
    void setNormalizeService(NormalizeService normalizeService) {
        this.normalizeService = normalizeService
    }
}
