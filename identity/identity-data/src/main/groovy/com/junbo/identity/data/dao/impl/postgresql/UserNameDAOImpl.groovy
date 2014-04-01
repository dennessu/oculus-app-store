/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.identity.data.dao.UserNameDAO
import com.junbo.identity.data.entity.user.UserNameEntity
import com.junbo.sharding.annotations.SeedParam
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 3/18/14.
 */
@CompileStatic
class UserNameDAOImpl extends ShardedDAOBase implements UserNameDAO {
    @Override
    UserNameEntity get(@SeedParam Long id) {
        return (UserNameEntity)currentSession().get(UserNameEntity, id)
    }

    @Override
    UserNameEntity create(UserNameEntity entity) {
        currentSession().save(entity)
        return get(entity.id)
    }

    @Override
    UserNameEntity update(UserNameEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    void delete(@SeedParam Long id) {
        UserNameEntity entity = (UserNameEntity)currentSession().get(UserNameEntity, id)
        currentSession().delete(entity)
    }

    @Override
    UserNameEntity findByUserId(@SeedParam Long userId) {
        Criteria criteria = currentSession().createCriteria(UserNameEntity)
        criteria.add(Restrictions.eq('userId', userId))

        return criteria.list() == null ? null : (UserNameEntity)criteria.list().get(0)
    }
}
