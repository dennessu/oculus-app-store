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
import net.sf.ehcache.Ehcache
import org.springframework.beans.factory.annotation.Required

/**
 * AuthorizeCallbackFactory.
 */
@CompileStatic
abstract class AbstractAuthorizeCallbackFactory<T> implements AuthorizeCallbackFactory<T> {

    private RoleResource roleResource

    private RoleAssignmentResource roleAssignmentResource

    private GroupResource groupResource

    private UserGroupMembershipResource userGroupMembershipResource

    private Ehcache groupIdsByUserIdCache

    private Ehcache groupIdByNameCache

    private Ehcache roleCache

    private Ehcache roleAssignmentCache

    RoleResource getRoleResource() {
        return roleResource
    }

    @Required
    void setRoleResource(RoleResource roleResource) {
        this.roleResource = roleResource
    }

    RoleAssignmentResource getRoleAssignmentResource() {
        return roleAssignmentResource
    }

    @Required
    void setRoleAssignmentResource(RoleAssignmentResource roleAssignmentResource) {
        this.roleAssignmentResource = roleAssignmentResource
    }

    GroupResource getGroupResource() {
        return groupResource
    }

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    UserGroupMembershipResource getUserGroupMembershipResource() {
        return userGroupMembershipResource
    }

    @Required
    void setUserGroupMembershipResource(UserGroupMembershipResource userGroupMembershipResource) {
        this.userGroupMembershipResource = userGroupMembershipResource
    }

    Ehcache getGroupIdsByUserIdCache() {
        return groupIdsByUserIdCache
    }

    @Required
    void setGroupIdsByUserIdCache(Ehcache groupIdsByUserIdCache) {
        this.groupIdsByUserIdCache = groupIdsByUserIdCache
    }

    Ehcache getGroupIdByNameCache() {
        return groupIdByNameCache
    }

    @Required
    void setGroupIdByNameCache(Ehcache groupIdByNameCache) {
        this.groupIdByNameCache = groupIdByNameCache
    }

    Ehcache getRoleCache() {
        return roleCache
    }

    @Required
    void setRoleCache(Ehcache roleCache) {
        this.roleCache = roleCache
    }

    Ehcache getRoleAssignmentCache() {
        return roleAssignmentCache
    }

    @Required
    void setRoleAssignmentCache(Ehcache roleAssignmentCache) {
        this.roleAssignmentCache = roleAssignmentCache
    }

    abstract AuthorizeCallback<T> create(T entity)
}
