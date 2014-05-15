/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.RoleDAO
import com.junbo.identity.data.entity.role.RoleEntity
import groovy.transform.CompileStatic
import org.hibernate.Session

/**
 * RoleDAOImpl.
 */
@CompileStatic
class RoleDAOImpl extends BaseDAO implements RoleDAO {
    @Override
    RoleEntity create(RoleEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextId()
        }
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get((Long) entity.id)
    }

    @Override
    RoleEntity update(RoleEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get((Long) entity.id)
    }

    @Override
    RoleEntity get(Long roleId) {
        return (RoleEntity) currentSession(roleId).get(RoleEntity, roleId)
    }

    @Override
    RoleEntity findByRoleName(String name, String resourceType, Long resourceId, String subResourceType) {
        RoleEntity example = new RoleEntity()
        example.name = name
        example.resourceType = resourceType
        example.resourceId = resourceId
        example.subResourceType = subResourceType

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def roleIds = viewQuery.list()

            Long id = roleIds.empty ? null : roleIds.get(0)
            if (id != null) {
                return get(id)
            }
        }

        return null
    }
}
