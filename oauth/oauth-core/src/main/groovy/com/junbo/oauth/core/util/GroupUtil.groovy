/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.list.OrganizationListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.OrganizationResource
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * GroupUtil.
 */
class GroupUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupUtil)

    private GroupResource groupResource

    private OrganizationResource organizationResource

    private UserGroupMembershipResource userGroupMembershipResource

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    @Required
    void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource
    }

    @Required
    void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource
    }

    Boolean hasGroups(UserId userId, String... groups) {
        for (String token : groups) {
            String[] tokens = token.split('\\.')
            assert tokens.length == 2
            String organizationName = tokens[0]
            String groupName = tokens[1]

            try {
                Results<Organization> organizationResults = organizationResource.list(new OrganizationListOptions(name: organizationName)).get()
                organizationResults.items.retainAll { Organization organization ->
                    organization.isValidated
                }

                assert organizationResults.items.size() == 1
                Organization organization = organizationResults.items.get(0)

                GroupListOptions groupOptions = new GroupListOptions(
                        organizationId: organization.getId(),
                        name: groupName
                )

                Results<Group> groupResults = groupResource.list(groupOptions).get()

                assert !groupResults.items.isEmpty()
                Group group = groupResults.items.get(0)

                UserGroupListOptions userGroupOptions = new UserGroupListOptions(
                        userId: userId,
                        groupId: group.getId()
                )

                Results<UserGroup> userGroupResults = userGroupMembershipResource.list(userGroupOptions).get()

                if (!userGroupResults.items.isEmpty()) {
                    return true
                }
            } catch (Exception e) {
                LOGGER.error('Exception happened during checking the group', e)
                return false
            }
        }
        return false
    }
}
