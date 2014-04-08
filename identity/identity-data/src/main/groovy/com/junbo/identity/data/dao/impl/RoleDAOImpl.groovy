/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.RoleDAO
import com.junbo.identity.data.entity.role.RoleEntity
import com.junbo.sharding.annotations.SeedParam
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions

/**
 * RoleDAOImpl.
 */
@CompileStatic
class RoleDAOImpl extends ShardedDAOBase implements RoleDAO {
    @Override
    RoleEntity create(RoleEntity entity) {
        currentSession().save(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    RoleEntity update(RoleEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    RoleEntity get(@SeedParam Long roleId) {
        return (RoleEntity) currentSession().get(RoleEntity, roleId)
    }

    @Override
    RoleEntity findByRoleName(String roleName) {
        Criteria criteria = currentSession().createCriteria(RoleEntity)
        criteria.add(Restrictions.eq('name', roleName))
        return (RoleEntity) criteria.uniqueResult()
    }

    @Override
    List<RoleEntity> findByResourceId(String resourceType, @SeedParam Long resourceId, String subResourceType) {
        Criteria criteria = currentSession().createCriteria(RoleEntity)
        if (resourceType != null) {
            criteria.add(Restrictions.eq('resourceType', resourceType))
        }

        if (resourceId != null) {
            criteria.add(Restrictions.eq('resourceId', resourceId))
        }

        if (subResourceType != null) {
            criteria.add(Restrictions.eq('subResourceType', subResourceType))
        }

        return criteria.list()
    }
}
