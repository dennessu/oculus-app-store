package com.junbo.data.handler

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by haomin on 14-7-11.
 */
@CompileStatic
class GroupDataHandler extends BaseDataHandler {
    private GroupResource groupResource
    private OrganizationResource organizationResource

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Override
    void handle(String content) {
        Group group
        try {
            group = transcoder.decode(new TypeReference<Group>() {}, content) as Group
        } catch (Exception e) {
            logger.error("Error parsing group $content", e)
            exit()
        }

        String[] splits = group.name.split(':')
        if (splits.size() != 2) {
            logger.error("Error parsing group $content")
            exit()
        }

        String groupName = splits[0]
        String organizationName = splits[1]

        group.name = groupName
        logger.info("loading group: $groupName")

        Group existing = null
        Organization org = null
        try {
            Results<Organization> results = organizationResource.list(new OrganizationListOptions(name: organizationName)).get()
            if (results != null && results.items != null && results.items.size() == 1) {
                org = results.items.get(0)
            }

            Results<Group> groupResults = groupResource.list(new GroupListOptions(
                    organizationId: org.id as OrganizationId,
                    name: groupName)).get()

            if (groupResults != null && groupResults.items != null && groupResults.items.size() > 0) {
                if (groupResults.items.size() > 0) {
                    existing = groupResults.items.get(0)
                }
            }
        } catch (AppErrorException e) {
            logger.debug('error happens', e)
        }

        if (existing == null) {
            logger.debug("Create new group with name: $groupName")
            try {
                group.organizationId = org.id as OrganizationId
                groupResource.create(group).get()
            } catch (Exception e) {
                logger.error("Error creating group $groupName.", e)
            }
        }
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }
}
