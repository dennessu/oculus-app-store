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
import com.junbo.common.id.UniversalId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Results
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

        Object objectId = getEntityIdByPropertyPath(propertyPath)
        if (objectId == null) {
            return false
        }

        if (!(objectId instanceof UniversalId)) {
            throw new IllegalStateException("entityId: $objectId must be a UniversalId")
        }

        UniversalId entityId = (UniversalId) objectId
        RoleId roleId = getRoleId(entityId, roleName)
        if (roleId == null) {
            return false
        }

        List<GroupId> groupIds = getGroupIdsByUserId(currentUserId)

        return hasRoleAssignments(roleId, currentUserId, groupIds)
    }

    Boolean hasAnyRole(String propertyPath, String... roleNames) {
        if (propertyPath == null || propertyPath.empty) {
            throw new IllegalArgumentException('propertyPath is null or empty')
        }

        def currentUserId = AuthorizeContext.currentUserId
        if (currentUserId == null) {
            return false
        }

        Object objectId = getEntityIdByPropertyPath(propertyPath)
        if (objectId == null) {
            return false
        }

        if (!(objectId instanceof UniversalId)) {
            throw new IllegalStateException("entityId: $objectId must be a UniversalId")
        }

        UniversalId entityId = (UniversalId) objectId

        List<GroupId> groupIds = getGroupIdsByUserId(currentUserId)

        for(String roleName : roleNames){
            RoleId roleId = getRoleId(entityId, roleName)
            if (roleId == null) {
                continue
            }
            if(hasRoleAssignments(roleId, currentUserId, groupIds)){
                return true
            }
        }
        return false
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

        // sort the groupIds
        groupIds = groupIds.sort { GroupId groupdId -> groupdId.value }

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
        )).get();

        GroupId groupId = results.items.empty ? (GroupId) null : (GroupId) results.items.get(0).id
        factory.groupIdByNameCache.put(new Element(groupName, groupId))

        return groupId
    }

    protected RoleId getRoleId(UniversalId entityId, String roleName) {
        def tuple = new Tuple(entityId, roleName)
        Element cachedElement = factory.roleCache.get(tuple)
        if (cachedElement != null) {
            return (RoleId) cachedElement.objectValue
        }

        String targetType = IdUtil.getResourceType(entityId.class)
        String filterLink = IdUtil.toHref(entityId)

        Results<Role> roles = factory.roleResource.list(new RoleListOptions(
                name: roleName,
                targetType: targetType,
                filterType: RoleFilterType.SINGLEINSTANCEFILTER,
                filterLink: filterLink
        )).get();

        RoleId roleId = roles.items.empty ? (RoleId) null : (RoleId) roles.items.get(0).id

        factory.roleCache.put(new Element(tuple, roleId))
        return roleId
    }

    protected boolean hasRoleAssignments(RoleId roleId, UserId userId, List<GroupId> groupIds) {
        def tuple = new Tuple(roleId, userId, groupIds)
        Element cachedElement = factory.roleAssignmentCache.get(tuple)
        if (cachedElement != null) {
            return (boolean) cachedElement.objectValue
        }

        def assignees = []

        assignees.add(IdUtil.toHref(userId))
        for (GroupId groupId : groupIds) {
            assignees.add(IdUtil.toHref(groupId))
        }

        boolean result = false
        for (String assignee : assignees) {
            Results<RoleAssignment> roleAssignments =
                    factory.roleAssignmentResource.list(new RoleAssignmentListOptions(
                            roleId: roleId,
                            assignee: assignee
                    )).get();
            if (!roleAssignments.items.empty) {
                result = true
                break
            }
        }

        factory.roleAssignmentCache.put(new Element(tuple, result))
        return result
    }
}
