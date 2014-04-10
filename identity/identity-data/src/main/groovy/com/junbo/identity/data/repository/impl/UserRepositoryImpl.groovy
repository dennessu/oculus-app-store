/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserId
import com.junbo.identity.data.dao.UserDAO
import com.junbo.identity.data.entity.user.UserEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Implementation for User DAO..
 */
@Component
@CompileStatic
class UserRepositoryImpl implements UserRepository {
    @Autowired
    private ModelMapper modelMapper

    @Autowired
    private UserDAO userDAO

    @Override
    Promise<User> create(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext())
        userEntity = userDAO.save(userEntity)

        return get(new UserId((Long)(userEntity.id)))
    }

    @Override
    Promise<User> update(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext())
        userDAO.update(userEntity)

        return get((UserId)user.id)
    }

    @Override
    Promise<User> get(UserId userId) {
        User user = modelMapper.toUser(userDAO.get(userId.value), new MappingContext())

        return Promise.pure(user)
    }

    @Override
    Promise<Void> delete(UserId userId) {
        userDAO.delete(userId.value)

        return Promise.pure(null)
    }

    @Override
    Promise<User> getUserByCanonicalUsername(String canonicalUsername) {
        if (StringUtils.isEmpty(canonicalUsername)) {
            throw new IllegalArgumentException('canonicalUsername is empty')
        }

        UserEntity entity = userDAO.getIdByCanonicalUsername(canonicalUsername)
        if (entity == null) {
            return Promise.pure(null)
        }
        return get(new UserId((Long)(entity.id)))
    }
}
