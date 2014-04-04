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

        user.id = idGenerator.nextIdByShardId(shardAlgorithm.shardId())

        currentSession(user.id).save(user)
        currentSession(user.id).flush()

        return get((Long) user.id)
    }

    @Override
    UserEntity update(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        currentSession(user.id).merge(user)
        currentSession(user.id).flush()

        return get((Long) user.id)
    }

    @Override
    UserEntity get(Long userId) {
        return (UserEntity) currentSession(userId).get(UserEntity, userId)
    }

    @Override
    void delete(Long userId) {
        UserEntity entity = (UserEntity) currentSession(userId).get(UserEntity, userId)
        currentSession(userId).delete(entity)
        currentSession(userId).flush()
    }

    @Override
    UserEntity getIdByCanonicalUsername(String username) {
        UserEntity example = new UserEntity()
        example.setUsername(username)

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
