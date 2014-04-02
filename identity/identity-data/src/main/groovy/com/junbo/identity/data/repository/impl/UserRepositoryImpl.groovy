/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserId
import com.junbo.identity.data.dao.UserDAO
import com.junbo.identity.data.dao.UserNameDAO

import com.junbo.identity.data.entity.user.UserEntity
import com.junbo.identity.data.entity.user.UserNameEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.model.users.UserName
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
    @Qualifier('userDAO')
    private UserDAO userDAO

    @Autowired
    @Qualifier('userNameDAO')
    private UserNameDAO userNameDAO

    @Override
    Promise<User> create(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext())
        userEntity = userDAO.save(userEntity)

        // create name structure
        UserNameEntity userNameEntity = modelMapper.toUserName(user.name, new MappingContext())
        userNameEntity.setUserId((Long)(userEntity.id))
        userNameDAO.create(userNameEntity)

        return get(new UserId((Long)(userEntity.id)))
    }

    @Override
    Promise<User> update(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext())
        userDAO.update(userEntity)

        UserNameEntity userNameEntity = modelMapper.toUserName(user.name, new MappingContext())
        UserNameEntity existingUserNameEntity = userNameDAO.findByUserId((Long)(userEntity.id))
        userNameEntity.setId(existingUserNameEntity.id)
        userNameEntity.setUserId(existingUserNameEntity.userId)
        userNameDAO.update(userNameEntity)

        return get((UserId)user.id)
    }

    @Override
    Promise<User> get(UserId userId) {
        User user = modelMapper.toUser(userDAO.get(userId.value), new MappingContext())
        if (user == null) {
            return Promise.pure(null)
        }
        UserName userName = modelMapper.toUserName(userNameDAO.findByUserId(userId.value), new MappingContext())
        user.setName(userName)

        return Promise.pure(user)
    }

    @Override
    Promise<Void> delete(UserId userId) {
        UserNameEntity userNameEntity = userNameDAO.findByUserId(userId.value)
        userNameDAO.delete(userNameEntity.id)

        userDAO.delete(userId.value)

        return Promise.pure(null)
    }

    @Override
    Promise<User> getUserByCanonicalUsername(String canonicalUsername) {
        if (StringUtils.isEmpty(canonicalUsername)) {
            throw new IllegalArgumentException('canonicalUsername is empty')
        }

        Long id = userDAO.getIdByCanonicalUsername(canonicalUsername)
        UserEntity entity = userDAO.get(id)
        return get(new UserId((Long)(entity.id)))
    }
}
