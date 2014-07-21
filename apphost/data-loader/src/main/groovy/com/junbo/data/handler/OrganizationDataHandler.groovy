package com.junbo.data.handler

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.data.model.OrganizationData
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by haomin on 14-7-11.
 */
@CompileStatic
class OrganizationDataHandler extends BaseDataHandler {
    private OrganizationResource organizationResource
    private UserResource userResource

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    void handle(String content) {
        OrganizationData organizationData
        try {
            organizationData = transcoder.decode(new TypeReference<OrganizationData>() {}, content) as OrganizationData
        } catch (Exception e) {
            logger.error("Error parsing organization $content", e)
            exit()
        }

        String orgName = organizationData.organizationName
        String ownerUsername = organizationData.organizationOwner

        Organization organization = new Organization()
        organization.name = orgName
        organization.isValidated = true

        Organization existing = null
        User orgOwnerUser = null
        try {
            Results<User> results = userResource.list(new UserListOptions(username: ownerUsername)).get()
            if (results != null && results.items != null && results.items.size() > 0) {
                orgOwnerUser = results.items.get(0)
            }

            Results<Organization> orgResults = organizationResource.list(new OrganizationListOptions(ownerId: orgOwnerUser.id as UserId)).get()
            if (orgResults != null && orgResults.items != null && orgResults.items.size() > 0) {
                orgResults.items.retainAll{ Organization organization1 ->
                    organization1.name == orgName
                }

                if (orgResults.items.size() > 0) {
                    existing = orgResults.items.get(0)
                }
            }
        } catch (AppErrorException e) {
            logger.debug('error happens', e)
        }

        if (existing == null) {
            logger.debug("Create new organization with name: $orgName")
            try {
                organization.ownerId = orgOwnerUser.id as UserId
                organizationResource.create(organization).get()
            } catch (Exception e) {
                logger.error("Error creating organization $orgName.", e)
            }
        }
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }
}
