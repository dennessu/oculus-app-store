/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

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
import com.junbo.oauth.core.context.ActionContextWrapper
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * ScopePreconditionFactory.
 */
@CompileStatic
class ScopePreconditionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScopePreconditionFactory)
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

    ScopePrecondition create(ActionContextWrapper context) {
        return new ScopePrecondition(this, context)
    }

    public static class ScopePrecondition {
        private ActionContextWrapper context
        private ScopePreconditionFactory factory

        ScopePrecondition(ScopePreconditionFactory factory, ActionContextWrapper context) {
            this.factory = factory
            this.context = context
        }

        Boolean hasGroups(String... groups) {
            def loginState = context.loginState
            assert loginState != null : 'loginState is null'
            UserId userId = new UserId(loginState.userId)

            for (String token : groups) {
                String[] tokens = token.split('\\.')
                assert tokens.length == 2
                String organizationName = tokens[0]
                String groupName = tokens[1]

                try {
                    Results<Organization> organizationResults = factory
                            .organizationResource.list(new OrganizationListOptions(name: organizationName)).get()

                    assert !organizationResults.items.isEmpty()
                    Organization organization = organizationResults.items.get(0)

                    GroupListOptions groupOptions = new GroupListOptions(
                            organizationId: organization.getId(),
                            name: groupName
                    )

                    Results<Group> groupResults = factory.groupResource.list(groupOptions).get()

                    assert !groupResults.items.isEmpty()
                    Group group = groupResults.items.get(0)

                    UserGroupListOptions userGroupOptions = new UserGroupListOptions(
                            userId: userId,
                            groupId: group.getId()
                    )

                    Results<UserGroup> userGroupResults = factory.userGroupMembershipResource.list(userGroupOptions).get()

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
}
