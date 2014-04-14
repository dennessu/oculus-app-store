/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.rest.auth;

import com.junbo.identity.spec.v1.resource.GroupResource;
import com.junbo.identity.spec.v1.resource.RoleAssignmentResource;
import com.junbo.identity.spec.v1.resource.RoleResource;
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource;
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * ItemAuthorizeCallbackFactoryBean.
 */
@CompileStatic
public class ItemAuthorizeCallbackFactoryBean implements FactoryBean<ItemAuthorizeCallback> {
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
    public ItemAuthorizeCallback getObject() throws Exception {
        ItemAuthorizeCallback callback = new ItemAuthorizeCallback();
        callback.setGroupResource(groupResource);
        callback.setUserGroupMembershipResource(userGroupMembershipResource);
        callback.setRoleResource(roleResource);
        callback.setRoleAssignmentResource(roleAssignmentResource);

        return callback;
    }

    @Override
    public Class<?> getObjectType() {
        return ItemAuthorizeCallback.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
