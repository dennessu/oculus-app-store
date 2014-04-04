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
import org.hibernate.Session
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

        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()
        return get(entity.id)
    }

    @Override
    UserNameEntity update(UserNameEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    void delete(Long id) {
        Session session = currentSession(id)
        UserNameEntity entity = (UserNameEntity)session.get(UserNameEntity, id)
        session.delete(entity)
        session.flush()
    }

    @Override
    UserNameEntity findByUserId(Long userId) {
        Criteria criteria = currentSession(userId).createCriteria(UserNameEntity)
        criteria.add(Restrictions.eq('userId', userId))

        return criteria.list() == null ? null : (UserNameEntity)criteria.list().get(0)
    }
}
