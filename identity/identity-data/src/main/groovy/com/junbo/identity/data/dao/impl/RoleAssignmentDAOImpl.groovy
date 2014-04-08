/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.RoleAssignmentDAO
import com.junbo.identity.data.entity.role.RoleAssignmentEntity
import com.junbo.sharding.annotations.SeedParam
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions

/**
 * RoleAssignmentDAOImpl.
 */
@CompileStatic
class RoleAssignmentDAOImpl extends ShardedDAOBase implements RoleAssignmentDAO {
    @Override
    RoleAssignmentEntity create(RoleAssignmentEntity entity) {
        currentSession().save(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    RoleAssignmentEntity get(@SeedParam Long roleAssignmentId) {
        return (RoleAssignmentEntity) currentSession().get(RoleAssignmentEntity, roleAssignmentId)
    }

    @Override
    RoleAssignmentEntity update(RoleAssignmentEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    RoleAssignmentEntity findByRoleIdAssignee(@SeedParam Long roleId, String assigneeType, Long assigneeId) {
        Criteria criteria = currentSession().createCriteria(RoleAssignmentEntity)
        if (roleId != null) {
            criteria.add(Restrictions.eq('roleId', roleId))
        }

        if (assigneeType != null) {
            criteria.add(Restrictions.eq('assigneeType', assigneeType))
        }

        if (assigneeId != null) {
            criteria.add(Restrictions.eq('assigneeId', assigneeId))
        }

        return (RoleAssignmentEntity) criteria.uniqueResult()
    }
}
