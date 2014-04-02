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
class UserDAOImpl extends ShardedDAOBase implements UserDAO {
    @Override
    UserEntity save(UserEntity user) {
        currentSession().save(user)
        currentSession().flush()

        return get((Long)user.id)
    }

    @Override
    UserEntity update(UserEntity user) {
        currentSession().merge(user)
        currentSession().flush()

        return get((Long)user.id)
    }

    @Override
    UserEntity get(Long userId) {
        return (UserEntity)currentSession().get(UserEntity, userId)
    }

    @Override
    void delete(Long userId) {
        UserEntity entity = (UserEntity)currentSession().get(UserEntity, userId)
        currentSession().delete(entity)
        currentSession().flush()
    }

    @Override
    // todo:    Liangfu:    This is temporary hack for sharding.
    // Due to internal call won't go through proxy
    Long getIdByCanonicalUsername(String username) {
        UserEntity example = new UserEntity()
        example.setUsername(username)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def userIds = viewQuery.list()

            return CollectionUtils.isEmpty(userIds) ? null : (Long)(userIds.get(0))
        }

        throw new RuntimeException()
    }
}
