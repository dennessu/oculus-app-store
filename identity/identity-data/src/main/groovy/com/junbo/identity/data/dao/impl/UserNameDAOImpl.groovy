/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserNameDAO
import com.junbo.identity.data.entity.user.UserNameEntity
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 3/18/14.
 */
@CompileStatic
class UserNameDAOImpl extends BaseDAO implements UserNameDAO {
    @Override
    UserNameEntity get(Long id) {
        return (UserNameEntity)currentSession(id).get(UserNameEntity, id)
    }

    @Override
    UserNameEntity create(UserNameEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        currentSession(entity.id).save(entity)
        currentSession(entity.id).flush()
        return get(entity.id)
    }

    @Override
    UserNameEntity update(UserNameEntity entity) {
        currentSession(entity.id).merge(entity)
        currentSession(entity.id).flush()

        return get(entity.id)
    }

    @Override
    void delete(Long id) {
        UserNameEntity entity = (UserNameEntity)currentSession(id).get(UserNameEntity, id)
        currentSession(id).delete(entity)
        currentSession(id).flush()
    }

    @Override
    UserNameEntity findByUserId(Long userId) {
        Criteria criteria = currentSession(userId).createCriteria(UserNameEntity)
        criteria.add(Restrictions.eq('userId', userId))

        return criteria.list() == null ? null : (UserNameEntity)criteria.list().get(0)
    }
}
