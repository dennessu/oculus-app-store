package com.junbo.identity.auth

import com.junbo.authorization.OwnerCallback
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UniversalId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import org.springframework.beans.factory.annotation.Required

class OrganizationOwnerCallback implements OwnerCallback {
    private OrganizationResource organizationResource

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Override
    UserId getUserOwnerId(UniversalId resourceId) {
        assert resourceId instanceof OrganizationId : 'resourceId is not an organizationId'
        Organization organization = organizationResource.get(resourceId as OrganizationId, new OrganizationGetOptions()).get()
        return organization.getOwnerId()
    }

    @Override
    OrganizationId getOrganizationOwnerId(UniversalId resourceId) {
        assert resourceId instanceof OrganizationId : 'resourceId is not an organizationId'
        return resourceId as OrganizationId
    }
}
