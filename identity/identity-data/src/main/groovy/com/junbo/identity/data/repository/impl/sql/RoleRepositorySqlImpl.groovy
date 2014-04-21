/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.RoleId
import com.junbo.identity.data.dao.RoleDAO
import com.junbo.identity.data.entity.role.RoleEntity
import com.junbo.identity.data.repository.RoleRepository
import com.junbo.identity.spec.v1.model.Role
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * RoleRepositorySqlImpl.
 */
@CompileStatic
class RoleRepositorySqlImpl implements RoleRepository {
    private RoleDAO roleDAO

    @Required
    void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO
    }

    @Override
    Promise<Role> create(Role role) {
        RoleEntity entity = roleDAO.create(unwrap(role))

        return Promise.pure(wrap(roleDAO.get((Long) entity.id)))
    }

    @Override
    Promise<Role> get(RoleId roleId) {
        RoleEntity entity = roleDAO.get(roleId.value)
        return Promise.pure(wrap(entity))
    }

    @Override
    Promise<Role> update(Role role) {
        RoleEntity entity = roleDAO.update(unwrap(role))

        return Promise.pure(wrap(roleDAO.get((Long) entity.id)))
    }

    @Override
    Promise<Void> delete(RoleId id) {
        throw new IllegalStateException('delete role not support')
    }

    @Override
    Promise<Role> findByRoleName(String roleName, String resourceType,
                                 Long resourceId, String subResourceType) {
        return Promise.pure(wrap(roleDAO.findByRoleName(roleName, resourceType, resourceId, subResourceType)))
    }

    private static Role wrap(RoleEntity entity) {
        if (entity == null) {
            return null
        }

        return new Role(
                id: new RoleId((Long) entity.id),
                name: entity.name,
                resourceType: entity.resourceType,
                resourceId: entity.resourceId,
                subResourceType: entity.subResourceType,
                resourceAge: entity.resourceAge.toString(),
                createdBy: entity.createdBy,
                createdTime: entity.createdTime,
                updatedBy: entity.updatedBy,
                updatedTime: entity.updatedTime
        )
    }

    private static RoleEntity unwrap(Role entity) {
        if (entity == null) {
            return null
        }

        return new RoleEntity(
                id: entity.id == null ? null : ((RoleId) entity.id).value,
                name: entity.name,
                resourceType: entity.resourceType,
                resourceId: entity.resourceId,
                subResourceType: entity.subResourceType,
                resourceAge: Integer.parseInt(entity.resourceAge),
                createdBy: entity.createdBy,
                createdTime: entity.createdTime,
                updatedBy: entity.updatedBy,
                updatedTime: entity.updatedTime
        )
    }
}
