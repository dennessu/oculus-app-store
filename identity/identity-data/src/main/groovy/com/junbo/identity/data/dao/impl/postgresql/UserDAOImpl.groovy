/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserDAO
import com.junbo.identity.data.entity.user.UserEntity
import groovy.transform.CompileStatic

/**
 * Implementation for User DAO..
 */
@CompileStatic
class UserDAOImpl extends ShardedDAOBase implements UserDAO {
    @Override
    UserEntity save(UserEntity user) {
        currentSession().save(user)

        return get(user.id)
    }

    @Override
    UserEntity update(UserEntity user) {
        currentSession().merge(user)
        currentSession().flush()

        return get(user.id)
    }

    @Override
    UserEntity get(Long userId) {
        return (UserEntity)currentSession().get(UserEntity, userId)
    }

    @Override
    void delete(Long userId) {
        UserEntity entity = (UserEntity)currentSession().get(UserEntity, userId)
        currentSession().delete(entity)
    }
}
