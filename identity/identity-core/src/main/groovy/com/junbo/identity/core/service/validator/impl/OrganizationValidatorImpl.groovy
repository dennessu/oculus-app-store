package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.OrganizationId
import com.junbo.identity.core.service.validator.OrganizationValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Check owner exists
 * Created by liangfu on 5/22/14.
 */
@CompileStatic
class OrganizationValidatorImpl implements OrganizationValidator {

    private OrganizationRepository organizationRepository
    private UserRepository userRepository

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

        if (organization.ownerId == null) {
            throw AppErrors.INSTANCE.fieldRequired('owner').exception()
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

    @Required
    void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }
}
