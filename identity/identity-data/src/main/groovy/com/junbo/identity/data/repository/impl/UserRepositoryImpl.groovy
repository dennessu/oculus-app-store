/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserId
import com.junbo.identity.data.dao.UserDAO
import com.junbo.identity.data.dao.UserNameDAO
import com.junbo.identity.data.dao.index.UserNameReverseIndexDAO
import com.junbo.identity.data.entity.reverselookup.UserNameReverseIndexEntity
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
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('userDAO')
    private UserDAO userDAO

    @Autowired
    @Qualifier('userNameReverseIndexDAO')
    private UserNameReverseIndexDAO userNameReverseIndexDAO

    @Autowired
    @Qualifier('userNameDAO')
    private UserNameDAO userNameDAO

    @Override
    Promise<User> create(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext())
        userEntity = userDAO.save(userEntity)

        // create name structure
        UserNameEntity userNameEntity = modelMapper.toUserName(user.name, new MappingContext())
        userNameEntity.setUserId(userEntity.id)
        userNameDAO.save(userNameEntity)

        // build reverse lookup
        UserNameReverseIndexEntity reverseLookupEntity = new UserNameReverseIndexEntity()
        reverseLookupEntity.setUserId(userEntity.id)
        reverseLookupEntity.setUsername(userEntity.username)
        userNameReverseIndexDAO.save(reverseLookupEntity)

        return get(new UserId(userEntity.id))
    }

    @Override
    Promise<User> update(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext())
        UserEntity existing = userDAO.get(userEntity.id)

        if (userEntity.username != existing.username) {
            userNameReverseIndexDAO.delete(existing.username)

            UserNameReverseIndexEntity reverseLookupEntity = new UserNameReverseIndexEntity()
            reverseLookupEntity.setUserId(userEntity.id)
            reverseLookupEntity.setUsername(userEntity.username)
            userNameReverseIndexDAO.save(reverseLookupEntity)
        }
        else if (userEntity.active != existing.active) {
            UserNameReverseIndexEntity reverseLookupEntity = userNameReverseIndexDAO.get(userEntity.username)
            userNameReverseIndexDAO.update(reverseLookupEntity)
        }

        userDAO.update(userEntity)

        UserNameEntity userNameEntity = modelMapper.toUserName(user.name, new MappingContext())
        UserNameEntity existingUserNameEntity = userNameDAO.findByUserId(userEntity.id)
        userNameEntity.setId(existingUserNameEntity.id)
        userNameEntity.setUserId(existingUserNameEntity.userId)
        userNameDAO.update(userNameEntity)

        return get((UserId)user.id)
    }

    @Override
    Promise<User> get(UserId userId) {
        User user = modelMapper.toUser(userDAO.get(userId.value), new MappingContext())
        UserName userName = modelMapper.toUserName(userNameDAO.findByUserId(userId.value), new MappingContext())
        user.setName(userName)

        return Promise.pure(user)
    }

    @Override
    Promise<Void> delete(UserId userId) {
        UserEntity userEntity = userDAO.get(userId.value)
        UserNameEntity userNameEntity = userNameDAO.findByUserId(userId.value)
        userNameDAO.delete(userNameEntity.id)

        userNameReverseIndexDAO.delete(userEntity.username)
        userDAO.delete(userId.value)

        return Promise.pure(null)
    }

    @Override
    Promise<User> getUserByCanonicalUsername(String canonicalUsername) {
        if (StringUtils.isEmpty(canonicalUsername)) {
            throw new IllegalArgumentException('canonicalUsername is empty')
        }

        UserNameReverseIndexEntity reverseEntity = userNameReverseIndexDAO.get(canonicalUsername)

        if (reverseEntity == null) {
            return Promise.pure(null)
        }

        return get(new UserId(reverseEntity.userId))
    }
}
