package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
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

        if (options.ownerId == null) {
            throw AppErrors.INSTANCE.parameterRequired('owner').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Organization organization) {
        if (organization.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        return checkBasicOrganizationInfo(organization)
    }

    @Override
    Promise<Void> validateForUpdate(OrganizationId organizationId, Organization organization,
                                    Organization oldOrganization) {
        if (organization.id == null) {
            throw AppErrors.INSTANCE.fieldRequired('id').exception()
        }
        if (organizationId != organization.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id', organizationId.toString()).exception()
        }
        if (organization.id != oldOrganization.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id', oldOrganization.id.toString()).exception()
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
                throw AppErrors.INSTANCE.fieldInvalid('type').exception()
            }
        }

        if (organization.taxType != null) {
            if (!OrganizationTaxType.values().any { OrganizationTaxType taxType ->
                return taxType.toString() == organization.taxType
            }) {
                throw AppErrors.INSTANCE.fieldInvalid('taxType').exception()
            }
        }

        if (organization.ownerId == null) {
            throw AppErrors.INSTANCE.fieldRequired('owner').exception()
        }

        organization.canonicalName = normalizeService.normalize(organization.name)

        // todo:    Need to add validation according to the requirement
        return checkOrganizationNameUnique(organization).then {
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
            // Todo:    This validation may be changed later according to oculus's new requirement.
            if (organization.type != OrganizationType.INDIVIDUAL.toString() || organization.taxType != UserPersonalInfoType.SSN.toString()) {
                throw AppErrors.INSTANCE.fieldInvalidException('type', 'type can only support INDIVIDUAL, taxType can only support SSN.').exception()
            }
            return checkPersonalInfoIdOwner(organization.taxId, organization.ownerId, UserPersonalInfoType.SSN.toString())
        }.then {
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

    private Promise<Void> checkOrganizationNameUnique(Organization organization) {
        return organizationRepository.searchByCanonicalName(organization.canonicalName, Integer.MAX_VALUE, 0).then { List<Organization> organizationList ->
            if (CollectionUtils.isEmpty(organizationList)) {
                return Promise.pure(null)
            }

            organizationList.retainAll { Organization org ->
                return org.id != organization.id
            }

            if (!CollectionUtils.isEmpty(organizationList)) {
                throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> checkPersonalInfoIdOwner(UserPersonalInfoId userPersonalInfoId, UserId ownerId, String expectedType) {
        return userPersonalInfoRepository.get(userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoId).exception()
            }

            if (userPersonalInfo.type != expectedType) {
                throw AppErrors.INSTANCE.fieldInvalidException('userPersonalInfoId', 'Type isn\'t valid, expected: ' + expectedType).exception()
            }

            if (userPersonalInfo.userId != ownerId) {
                throw AppErrors.INSTANCE.fieldInvalidException('userPersonalInfoId', 'UserId isn\'t match with ownerId').exception()
            }

            return Promise.pure(null)
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
