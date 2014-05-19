/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.spec.model.Role
import com.junbo.authorization.spec.model.RoleAssignment
import com.junbo.authorization.spec.option.list.RoleAssignmentListOptions
import com.junbo.authorization.spec.option.list.RoleListOptions
import com.junbo.authorization.spec.option.model.RoleFilterType
import com.junbo.common.id.GroupId
import com.junbo.common.id.RoleId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.common.util.Identifiable
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import groovy.transform.CompileStatic
import net.sf.ehcache.Element

/**
 * AuthorizeCallback.
 */
@CompileStatic
abstract class AbstractAuthorizeCallback<T> implements AuthorizeCallback<T> {

    private final AbstractAuthorizeCallbackFactory<T> factory

    private final T entity

    AbstractAuthorizeCallbackFactory<T> getFactory() {
        return factory
    }

    abstract String getApiName()

    T getEntity() {
        return entity
    }

    protected AbstractAuthorizeCallback(AbstractAuthorizeCallbackFactory<T> factory, T entity) {
        if (factory == null) {
            throw new IllegalArgumentException('factory is null')
        }

        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        this.factory = factory

        this.entity = entity
    }

    protected UserId getUserOwnerId() {
        throw new RuntimeException("Unable to get userOwnerId from ${entity}")
    }

    protected String getClientOwnerId() {
        throw new RuntimeException("Unable to get clientOwnerId from ${entity}")
    }

    protected Object getEntityIdByPropertyPath(String propertyPath) {
        if (propertyPath == 'self') {
            if (entity instanceof Identifiable) {
                return ((Identifiable) entity).id
            }
        }

        throw new RuntimeException("Unable to get entityId from ${entity} with propertyPath ${propertyPath}")
    }

    Boolean getOwnedByCurrentUser() {
        def currentUserId = AuthorizeContext.currentUserId
        if (currentUserId == null) {
            return false
        }

        if (currentUserId == userOwnerId) {
            return true
        }

        return false
    }

    Boolean getOwnedByCurrentClient() {
        def currentClientId = AuthorizeContext.currentClientId
        if (currentClientId == null) {
            return false
        }

        if (currentClientId == clientOwnerId) {
            return true
        }

        return false
    }

    Boolean inGroup(String groupName) {
        def currentUserId = AuthorizeContext.currentUserId
        if (currentUserId == null) {
            return false
        }

        GroupId groupId = getGroupIdByName(groupName)
        if (groupId == null) {
            return false
        }

        List<GroupId> groupIds = getGroupIdsByUserId(currentUserId)

        return groupIds.contains(groupId)
    }

    private List<GroupId> getGroupIdsByUserId(UserId userId) {
        Element cachedElement = factory.groupIdsByUserIdCache.get(userId)
        if (cachedElement != null) {
            return (List<GroupId>) cachedElement.objectValue
        }

        Results<UserGroup> userGroups = factory.userGroupMembershipResource.list(new UserGroupListOptions(
                userId: userId
        )).get();

        List<GroupId> groupIds = userGroups.items.empty ?
                (List<GroupId>) Collections.emptyList() :
                userGroups.items.collect { UserGroup userGroup -> userGroup.groupId }

        factory.groupIdsByUserIdCache.put(new Element(userId, groupIds))
        return groupIds
    }

    private GroupId getGroupIdByName(String groupName) {
        Element cachedElement = factory.groupIdByNameCache.get(groupName)
        if (cachedElement != null) {
            return (GroupId) cachedElement.objectValue
        }

        Results<Group> results = factory.groupResource.list(new GroupListOptions(
                name: groupName
        )).get()

        GroupId groupId = results.items.empty ? (GroupId) null : (GroupId) results.items.get(0).id
        factory.groupIdByNameCache.put(new Element(groupName, groupId))

        return groupId
    }

    Boolean hasRole(String roleName) {
        return hasRole('self', roleName)
    }

    Boolean hasRole(String propertyPath, String roleName) {
        if (propertyPath == null || propertyPath.empty) {
            throw new IllegalArgumentException('propertyPath is null or empty')
        }

        if (roleName == null || roleName.empty) {
            throw new IllegalArgumentException('roleName is null or empty')
        }

        def currentUserId = AuthorizeContext.currentUserId
        if (currentUserId == null) {
            return false
        }

        Object entityId = getEntityIdByPropertyPath(propertyPath)
        if (entityId == null) {
            return false
        }

        // todo: use IdUtil
        String targetType = entityId.toString()
        String filterLink = entityId.toString()

        Results<Role> roles = factory.roleResource.list(new RoleListOptions(
                name: roleName,
                targetType: targetType,
                filterType: RoleFilterType.SINGLEINSTANCEFILTER,
                filterLink: filterLink
        )).get()

        if (roles.items.empty) {
            return false
        }

        Role role = roles.items.get(0)

        Results<UserGroup> userGroups = factory.userGroupMembershipResource.list(new UserGroupListOptions(
                userId: currentUserId
        )).get();


        def assignee = []

        // todo: hard code for now.
        assignee.add('/v1/users/' + IdFormatter.encodeId(currentUserId))
        for (UserGroup userGroup : userGroups.items) {
            assignee.add('/v1/groups/' + IdFormatter.encodeId(userGroup.groupId))
        }

        Results<RoleAssignment> roleAssignments = factory.roleAssignmentResource.list(new RoleAssignmentListOptions(
                roleId: (RoleId) role.id,
                assignee: assignee.join(',')
        )).get()


        if (roleAssignments.items.empty) {
            return false
        }

        return true
    }
}
