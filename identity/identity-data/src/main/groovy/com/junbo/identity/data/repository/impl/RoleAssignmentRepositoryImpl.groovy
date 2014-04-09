/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.RoleAssignmentId
import com.junbo.common.id.RoleId
import com.junbo.identity.data.dao.RoleAssignmentDAO
import com.junbo.identity.data.entity.role.RoleAssignmentEntity
import com.junbo.identity.data.repository.RoleAssignmentRepository
import com.junbo.identity.spec.v1.model.RoleAssignment
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * RoleAssignmentRepositoryImpl.
 */
@CompileStatic
class RoleAssignmentRepositoryImpl implements RoleAssignmentRepository {
    private RoleAssignmentDAO roleAssignmentDAO

    @Required
    void setRoleAssignmentDAO(RoleAssignmentDAO roleAssignmentDAO) {
        this.roleAssignmentDAO = roleAssignmentDAO
    }

    @Override
    Promise<RoleAssignment> create(RoleAssignment roleAssignment) {
        RoleAssignmentEntity entity = roleAssignmentDAO.create(unwrap(roleAssignment))

        return Promise.pure(wrap(roleAssignmentDAO.get((Long) entity.id)))
    }

    @Override
    Promise<RoleAssignment> get(RoleAssignmentId id) {
        return Promise.pure(wrap(roleAssignmentDAO.get(id.value)))
    }

    @Override
    Promise<RoleAssignment> update(RoleAssignment roleAssignment) {
        RoleAssignmentEntity entity = roleAssignmentDAO.update(unwrap(roleAssignment))

        return Promise.pure(wrap(roleAssignmentDAO.get((Long) entity.id)))
    }

    @Override
    Promise<RoleAssignment> findByRoleIdAssignee(RoleId roleId, String assigneeType, Long assigneeId) {
        return Promise.pure(wrap(roleAssignmentDAO.findByRoleIdAssignee(roleId.value, assigneeType, assigneeId)))
    }

    private static RoleAssignment wrap(RoleAssignmentEntity entity) {
        if (entity == null) {
            return null
        }

        return new RoleAssignment(
                id: new RoleAssignmentId((Long) entity.id),
                roleId: new RoleId(entity.roleId),
                assigneeType: entity.assigneeType,
                assigneeId: entity.assigneeId,
                resourceAge: entity.resourceAge,
                createdBy: entity.createdBy,
                createdTime: entity.createdTime,
                updatedBy: entity.updatedBy,
                updatedTime: entity.updatedTime
        )
    }

    private static RoleAssignmentEntity unwrap(RoleAssignment entity) {
        if (entity == null) {
            return null
        }

        return new RoleAssignmentEntity(
                id: entity.id == null ? null : ((RoleAssignmentId) entity.id).value,
                roleId: entity.roleId.value,
                assigneeType: entity.assigneeType,
                assigneeId: entity.assigneeId,
                resourceAge: entity.resourceAge,
                createdBy: entity.createdBy,
                createdTime: entity.createdTime,
                updatedBy: entity.updatedBy,
                updatedTime: entity.updatedTime
        )
    }
}
