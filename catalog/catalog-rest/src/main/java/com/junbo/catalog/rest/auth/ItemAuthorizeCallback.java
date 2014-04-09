/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.rest.auth;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Group;
import com.junbo.identity.spec.v1.model.Role;
import com.junbo.identity.spec.v1.model.RoleAssignment;
import com.junbo.identity.spec.v1.model.UserGroup;
import com.junbo.identity.spec.v1.option.list.GroupListOptions;
import com.junbo.identity.spec.v1.option.list.RoleAssignmentListOptions;
import com.junbo.identity.spec.v1.option.list.RoleListOptions;
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions;
import com.junbo.identity.spec.v1.resource.GroupResource;
import com.junbo.identity.spec.v1.resource.RoleAssignmentResource;
import com.junbo.identity.spec.v1.resource.RoleResource;
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource;
import com.junbo.oauth.spec.model.TokenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * ItemAuthorizeCallback.
 */
public class ItemAuthorizeCallback implements AuthorizeCallback<Item> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ItemAuthorizeCallback.class);
    private String apiName;
    private TokenInfo tokenInfo;
    private Long id;
    private Item entity;

    private RoleResource roleResource;
    private RoleAssignmentResource roleAssignmentResource;
    private GroupResource groupResource;
    private UserGroupMembershipResource userGroupMembershipResource;

    public void setRoleResource(RoleResource roleResource) {
        this.roleResource = roleResource;
    }

    public void setRoleAssignmentResource(RoleAssignmentResource roleAssignmentResource) {
        this.roleAssignmentResource = roleAssignmentResource;
    }

    public void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource;
    }

    public void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource;
    }

    @Override
    public String getApiName() {
        return apiName;
    }

    @Override
    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    @Override
    public boolean isInGroup(String groupName) {
        GroupListOptions groupListOptions = new GroupListOptions();
        groupListOptions.setName(groupName);
        try {
            Results<Group> results = groupResource.list(groupListOptions).wrapped().get();

            if (results.getItems().isEmpty()) {
                return false;
            }

            Group group = results.getItems().get(0);

            UserGroupListOptions userGroupListOptions = new UserGroupListOptions();
            userGroupListOptions.setGroupId(group.getId());
            userGroupListOptions.setUserId(tokenInfo.getSub());
            Results<UserGroup> userGroups = userGroupMembershipResource.list(userGroupListOptions).wrapped().get();

            return !userGroups.getItems().isEmpty();

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error calling external service.", e);
        }

        return false;
    }

    @Override
    public boolean hasRoleAssignment(String roleName) {
        RoleListOptions roleListOptions = new RoleListOptions();
        roleListOptions.setName(roleName);
        roleListOptions.setResourceType("item");
        roleListOptions.setResourceId(id);

        try {
            Results<Role> roles = roleResource.list(roleListOptions).wrapped().get();

            if (roles.getItems().isEmpty()) {
                return false;
            }

            Role role = roles.getItems().get(0);

            RoleAssignmentListOptions roleAssignmentListOptions = new RoleAssignmentListOptions();
            roleAssignmentListOptions.setRoleId(role.getId());
            roleAssignmentListOptions.setAssigneeType("user");
            roleAssignmentListOptions.setAssigneeId(tokenInfo.getSub().getValue());
            Results<RoleAssignment> roleAssignments = roleAssignmentResource.list(roleAssignmentListOptions)
                    .wrapped().get();

            return !roleAssignments.getItems().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error calling external service.", e);
        }

        return false;
    }

    @Override
    public AuthorizeCallback<Item> initialize(Map<String, Object> context) {
        this.apiName = (String) context.get("apiName");
        this.id = (Long) context.get("id");
        this.entity = (Item) context.get("entity");
        if (id == null && entity != null) {
            id = entity.getId();
        }
        this.tokenInfo = (TokenInfo) context.get("tokenInfo");
        return this;
    }

    @Override
    public Item postFilter() {
        return null;
    }
}
