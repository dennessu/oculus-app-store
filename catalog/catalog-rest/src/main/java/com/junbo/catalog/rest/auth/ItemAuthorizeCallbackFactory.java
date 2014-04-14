/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.rest.auth;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeCallbackFactory;
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
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * ItemAuthorizeCallbackFactoryBean.
 */
@CompileStatic
public class ItemAuthorizeCallbackFactory implements AuthorizeCallbackFactory<Item> {

    private RoleResource roleResource;

    private RoleAssignmentResource roleAssignmentResource;

    private GroupResource groupResource;

    private UserGroupMembershipResource userGroupMembershipResource;

    @Required
    public void setRoleResource(RoleResource roleResource) {
        this.roleResource = roleResource;
    }

    @Required
    public void setRoleAssignmentResource(RoleAssignmentResource roleAssignmentResource) {
        this.roleAssignmentResource = roleAssignmentResource;
    }

    @Required
    public void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource;
    }

    @Required
    public void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource;
    }

    @Override
    public AuthorizeCallback<Item> create(Map<String, Object> context) {
        return new ItemAuthorizeCallback(context);
    }

    @CompileStatic
    private class ItemAuthorizeCallback implements AuthorizeCallback<Item> {
        private TokenInfo tokenInfo;
        private String apiName;
        private Item entity;

        public ItemAuthorizeCallback(Map<String, Object> context) {
            this.apiName = (String) context.get("apiName");
            this.entity = (Item) context.get("entity");
        }

        @Override
        public String getApiName() {
            return apiName;
        }

        @Override
        public void setTokenInfo(TokenInfo tokenInfo) {
            this.tokenInfo = tokenInfo;
        }

        public boolean isOwner() {
            return tokenInfo.getSub().getValue().equals(entity.getOwnerId());
        }

        public boolean group(String groupName) {
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
                throw new RuntimeException(e);
            }
        }

        public boolean role(String roleName) {
            RoleListOptions roleListOptions = new RoleListOptions();
            roleListOptions.setName(roleName);
            roleListOptions.setResourceType("item");
            roleListOptions.setResourceId(entity.getItemId());

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
                throw new RuntimeException(e);
            }
        }

        @Override
        public Item postFilter() {
            return null;
        }
    }

}
