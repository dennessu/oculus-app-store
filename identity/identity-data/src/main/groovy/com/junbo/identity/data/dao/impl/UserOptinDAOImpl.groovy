/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserOptinDAO
import com.junbo.identity.data.entity.user.UserOptinEntity
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Restrictions

/**
 * Implementation for UserOptinDAO.
 */
@CompileStatic
class UserOptinDAOImpl extends BaseDAO implements UserOptinDAO {

    @Override
    UserOptinEntity save(UserOptinEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get((Long)entity.id)
    }

    @Override
    UserOptinEntity update(UserOptinEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get((Long)entity.id)
    }

    @Override
    UserOptinEntity get(Long id) {
        return (UserOptinEntity)currentSession(id).get(UserOptinEntity, id)
    }

    @Override
    List<UserOptinEntity> searchByUserId(Long userId) {
        Criteria criteria = currentSession(userId).createCriteria(UserOptinEntity)
        criteria.add(Restrictions.eq('userId', userId))
        return criteria.list()
    }

    @Override
    List<UserOptinEntity> searchByType(String type) {
        UserOptinEntity example = new UserOptinEntity()
        example.setType(type)

        def result = []
        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def userIds = viewQuery.list()

            userIds.each { Long userId ->
                result.add(get(userId))
            }
        }

        return result
    }

    @Override
    void delete(Long id) {
        Session session = currentSession(id)
        UserOptinEntity entity = (UserOptinEntity)session.get(UserOptinEntity, id)
        session.delete(entity)
        session.flush()
    }
}
