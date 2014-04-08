/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.RoleId
import com.junbo.identity.data.dao.RoleDAO
import com.junbo.identity.data.entity.role.RoleEntity
import com.junbo.identity.data.repository.RoleRepository
import com.junbo.identity.spec.v1.model.Role
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * RoleRepositoryImpl.
 */
@CompileStatic
class RoleRepositoryImpl implements RoleRepository {
    private RoleDAO roleDAO

    @Required
    void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO
    }

    @Override
    Promise<Role> create(Role role) {
        roleDAO.create(unwrap(role))

        return get(role.id)
    }

    @Override
    Promise<Role> get(RoleId roleId) {
        RoleEntity entity = roleDAO.get(roleId.value)
        return Promise.pure(wrap(entity))
    }

    @Override
    Promise<Role> update(Role role) {
        roleDAO.update(unwrap(role))

        return get(role.id)
    }

    @Override
    Promise<Role> findByRoleName(String roleName) {
        return Promise.pure(wrap(roleDAO.findByRoleName(roleName)))
    }

    @Override
    Promise<List<Role>> findByResourceId(String resourceType, Long resourceId, String subResourceType) {
        List<Role> list = roleDAO.findByResourceId(resourceType, resourceId, subResourceType)
                .collect { RoleEntity entity ->
            return wrap(entity)
        }

        return Promise.pure(list)
    }

    private static Role wrap(RoleEntity entity) {
        if (entity == null) {
            return null
        }

        return new Role(
                id: new RoleId(entity.id),
                name: entity.name,
                resourceType: entity.resourceType,
                resourceId: entity.resourceId,
                subResourceType: entity.subResourceType,
                resourceAge: entity.resourceAge,
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
                id: entity.id.value,
                name: entity.name,
                resourceType: entity.resourceType,
                resourceId: entity.resourceId,
                subResourceType: entity.subResourceType,
                resourceAge: entity.resourceAge,
                createdBy: entity.createdBy,
                createdTime: entity.createdTime,
                updatedBy: entity.updatedBy,
                updatedTime: entity.updatedTime
        )
    }
}
