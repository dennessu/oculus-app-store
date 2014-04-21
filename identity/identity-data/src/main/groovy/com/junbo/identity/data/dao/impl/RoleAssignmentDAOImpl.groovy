/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.RoleAssignmentDAO
import com.junbo.identity.data.entity.role.RoleAssignmentEntity
import groovy.transform.CompileStatic
import org.hibernate.Session

/**
 * RoleAssignmentDAOImpl.
 */
@CompileStatic
class RoleAssignmentDAOImpl extends BaseDAO implements RoleAssignmentDAO {
    @Override
    RoleAssignmentEntity create(RoleAssignmentEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextIdByShardId(shardAlgorithm.shardId())
        }
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get((Long) entity.id)
    }

    @Override
    RoleAssignmentEntity get(Long roleAssignmentId) {
        return (RoleAssignmentEntity) currentSession(roleAssignmentId).get(RoleAssignmentEntity, roleAssignmentId)
    }

    @Override
    RoleAssignmentEntity update(RoleAssignmentEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get((Long) entity.id)
    }

    @Override
    RoleAssignmentEntity findByRoleIdAssignee(Long roleId, String assigneeType, Long assigneeId) {
        RoleAssignmentEntity example = new RoleAssignmentEntity()
        example.roleId = roleId
        example.assigneeType = assigneeType
        example.assigneeId = assigneeId

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            Long id = ids.empty ? null : (Long) ids.get(0)

            if (id != null) {
                return get(id)
            }
        }

        return null
    }
}
