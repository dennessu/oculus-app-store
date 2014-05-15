/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.spec.resource.RoleAssignmentResource
import com.junbo.authorization.spec.resource.RoleResource
import com.junbo.identity.spec.v1.resource.GroupResource
import com.junbo.identity.spec.v1.resource.UserGroupMembershipResource
import groovy.transform.CompileStatic

/**
 * AuthorizeCallbackFactory.
 */
@CompileStatic
abstract class AbstractAuthorizeCallbackFactory<T> implements AuthorizeCallbackFactory<T> {

    private RoleResource roleResource;

    private RoleAssignmentResource roleAssignmentResource;

    private GroupResource groupResource;

    private UserGroupMembershipResource userGroupMembershipResource;

    RoleResource getRoleResource() {
        return roleResource
    }

    void setRoleResource(RoleResource roleResource) {
        this.roleResource = roleResource
    }

    RoleAssignmentResource getRoleAssignmentResource() {
        return roleAssignmentResource
    }

    void setRoleAssignmentResource(RoleAssignmentResource roleAssignmentResource) {
        this.roleAssignmentResource = roleAssignmentResource
    }

    GroupResource getGroupResource() {
        return groupResource
    }

    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    UserGroupMembershipResource getUserGroupMembershipResource() {
        return userGroupMembershipResource
    }

    void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource
    }

    abstract AuthorizeCallback<T> create(String apiName, T entity)
}
