package com.junbo.data.handler

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.data.model.UserGroupData
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by haomin on 7/12/14.
 */
@CompileStatic
class UserGroupDataHandler extends BaseDataHandler {
    private UserResource userResource
    private GroupResource groupResource
    private OrganizationResource organizationResource
    private UserGroupMembershipResource userGroupMembershipResource

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    @Required
    void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource
    }

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Override
    void handle(String content) {
        UserGroupData userGroupDataData
        try {
            userGroupDataData = transcoder.decode(new TypeReference<UserGroupData>() {}, content) as UserGroupData
        } catch (Exception e) {
            logger.error("Error parsing user group $content", e)
            exit()
        }

        UserGroup userGroup = new UserGroup()

        UserGroup existing = null
        User user = null
        Group group = null
        Organization organization = null
        User organizationOwner = null
        try {
            Results<User> userResults = userResource.list(new UserListOptions(username: userGroupDataData.organizationOwner)).get()
            if (userResults != null && userResults.items != null && userResults.items.size() > 0) {
                organizationOwner = userResults.items.get(0)
            }

            Results<Organization> organizationResults = organizationResource.list(new OrganizationListOptions(ownerId: organizationOwner.id as UserId)).get()
            if (organizationResults != null && organizationResults.items != null && organizationResults.items.size() > 0) {
                organizationResults.items.retainAll{ Organization organization1 ->
                    organization1.name == userGroupDataData.organizationName
                }

                if (organizationResults.items.size() > 0) {
                    organization = organizationResults.items.get(0)
                }
            }

            Results<Group> groupResults = groupResource.list(
                    new GroupListOptions(organizationId: organization.id as OrganizationId, name: userGroupDataData.groupName)).get()

            if (groupResults != null && groupResults.items != null && groupResults.items.size() > 0) {
                if (groupResults.items.size() > 0) {
                    group = groupResults.items.get(0)
                }
            }

            Results<User> results = userResource.list(new UserListOptions(username: userGroupDataData.username)).get()
            if (results != null && results.items != null && results.items.size() > 0) {
                user = results.items.get(0)
            }

            Results<UserGroup> userGroupResults = userGroupMembershipResource.list(new UserGroupListOptions(
                    userId: user.id as UserId,
                    groupId: group.id as GroupId
            )).get()

            if (userGroupResults != null && userGroupResults.items != null && userGroupResults.items.size() > 0) {
                existing = userGroupResults.items.get(0)
            }

            /*
            Results<User> results2 = userResource.list(new UserListOptions(groupId: group.id as GroupId)).get()
            if (results2 != null && results2.items != null && results2.items.size() > 0) {
                results2.items.removeAll{ User u ->
                    u.id != user.id
                }

                if (results.items.size() > 0) {
                    exist = true
                }
            }*/
        } catch (AppErrorException e) {
            logger.debug('error happens', e)
        }

        if (existing == null) {
            userGroup.groupId = group.id as GroupId
            userGroup.userId = user.id as UserId

            logger.debug("Create new userGroup")
            try {
                userGroupMembershipResource.create(userGroup).get()
            } catch (Exception e) {
                logger.info("Error creating userGroup.", e)
            }
        }
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }
}
