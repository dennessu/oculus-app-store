/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserCommunicationDAO
import com.junbo.identity.data.entity.user.UserCommunicationEntity
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Restrictions

/**
 * Implementation for UserCommunicationDAO.
 */
@CompileStatic
class UserCommunicationDAOImpl extends BaseDAO implements UserCommunicationDAO {

    @Override
    UserCommunicationEntity save(UserCommunicationEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextId(entity.userId)
        }
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get((Long)entity.id)
    }

    @Override
    UserCommunicationEntity update(UserCommunicationEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get((Long)entity.id)
    }

    @Override
    UserCommunicationEntity get(Long id) {
        return (UserCommunicationEntity)currentSession(id).get(UserCommunicationEntity, id)
    }

    @Override
    List<UserCommunicationEntity> searchByUserId(Long userId) {
        Criteria criteria = currentSession(userId).createCriteria(UserCommunicationEntity)
        criteria.add(Restrictions.eq('userId', userId))
        return criteria.list()
    }

    @Override
    List<UserCommunicationEntity> searchByCommunicationId(Long communicationId) {
        UserCommunicationEntity example = new UserCommunicationEntity()
        example.setCommunicationId(communicationId)

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
        UserCommunicationEntity entity = (UserCommunicationEntity)session.get(UserCommunicationEntity, id)
        session.delete(entity)
        session.flush()
    }
}
