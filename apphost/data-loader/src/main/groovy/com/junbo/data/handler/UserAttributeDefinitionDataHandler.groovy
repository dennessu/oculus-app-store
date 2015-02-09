package com.junbo.data.handler

import com.junbo.common.model.Results
import com.junbo.data.model.UserAttributeDefinitionData
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.list.UserAttributeDefinitionListOptions
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserAttributeDefinitionResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource
import org.springframework.util.CollectionUtils

/**
 * Created by dell on 2/9/2015.
 */
@CompileStatic
class UserAttributeDefinitionDataHandler extends BaseDataHandler {
    private OrganizationResource organizationResource;
    private UserAttributeDefinitionResource userAttributeDefinitionResource;

    @Override
    void handle(String content) {
        UserAttributeDefinitionData userAttributeDefinitionData
        try {
            userAttributeDefinitionData = transcoder.decode(new TypeReference<UserAttributeDefinitionData>() {}, content) as UserAttributeDefinitionData
        } catch (Exception e) {
            logger.error("Error parsing userAttributeDefinitionData $content", e)
            exit()
        }

        String organizationName = userAttributeDefinitionData.organizationName;
        String userAttributeDefinitionType = userAttributeDefinitionData.type;

        Results<Organization> organizationResults = organizationResource.list(new OrganizationListOptions(
             name: organizationName
        )).get()

        organizationResults.items.retainAll { Organization org ->
            org.isValidated
        }

        if (organizationResults.getItems().size() != 1) {
            logger.error("organization with name $organizationName not found")
            return
        }

        Organization organization = organizationResults.getItems().get(0)
        UserAttributeDefinition userAttributeDefinition = new UserAttributeDefinition(
              type: userAttributeDefinitionType,
              organizationId: organization.getId(),
              description: "$organizationName user attribute definition"
        )

        Results<UserAttributeDefinition> results = userAttributeDefinitionResource.list(new UserAttributeDefinitionListOptions(
                organizationId: organization.getId(),
                type: userAttributeDefinitionType
        )).get()

        if (CollectionUtils.isEmpty(results.items)) {
            // not found, create
            try {
                userAttributeDefinitionResource.create(userAttributeDefinition).get()
            } catch (Exception e) {
                logger.error("create userAttributeDefinition with name $organizationName error", e)
            }
        } else {
            // find, do nothing
            logger.info("$organizationName has $userAttributeDefinitionType userAttributeDefinition")
        }
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setUserAttributeDefinitionResource(UserAttributeDefinitionResource userAttributeDefinitionResource) {
        this.userAttributeDefinitionResource = userAttributeDefinitionResource
    }
}
