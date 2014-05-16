/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserDAO
import com.junbo.identity.data.entity.user.UserEntity
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.hibernate.Session

/**
 * Implementation for User DAO..
 */
@CompileStatic
class UserDAOImpl extends BaseDAO implements UserDAO {

    @Override
    UserEntity save(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }
        if (user.id == null) {
            user.id = idGenerator.nextId()
        }
        Session session = currentSession(user.id)
        session.save(user)
        session.flush()

        return get((Long) user.id)
    }

    @Override
    UserEntity update(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        Session session = currentSession(user.id)
        session.merge(user)
        session.flush()

        return get((Long) user.id)
    }

    @Override
    UserEntity get(Long userId) {
        return (UserEntity) currentSession(userId).get(UserEntity, userId)
    }

    @Override
    void delete(Long userId) {
        Session session = currentSession(userId)
        UserEntity entity = (UserEntity) session.get(UserEntity, userId)
        session.delete(entity)
        session.flush()
    }

    @Override
    UserEntity getIdByCanonicalUsername(String username) {
        UserEntity example = new UserEntity()
        example.setCanonicalUsername(username)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def userIds = viewQuery.list()

            Long userId = CollectionUtils.isEmpty(userIds) ? null : (Long) (userIds.get(0))

            if (userId != null) {
                return get(userId)
            }
        }

        return null
    }
}
