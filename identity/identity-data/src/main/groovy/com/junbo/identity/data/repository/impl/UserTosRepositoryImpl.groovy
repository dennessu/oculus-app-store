/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserTosId
import com.junbo.identity.data.dao.UserTosDAO
import com.junbo.identity.data.entity.user.UserTosEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.spec.model.users.UserTos
import com.junbo.identity.spec.options.list.UserTosListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Implementation for User Tos Acceptance DAO interface.
 */
@Component
@CompileStatic
class UserTosRepositoryImpl implements UserTosRepository {
    @Autowired
    @Qualifier('userTosDAO')
    private UserTosDAO userTosDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserTos> create(UserTos entity) {
        UserTosEntity userTosAcceptanceEntity = modelMapper.toUserTos(entity, new MappingContext())
        userTosDAO.save(userTosAcceptanceEntity)
        return get(new UserTosId(userTosAcceptanceEntity.id))
    }

    @Override
    Promise<UserTos> update(UserTos entity) {
        UserTosEntity userTosAcceptanceEntity = modelMapper.toUserTos(entity, new MappingContext())
        userTosDAO.update(userTosAcceptanceEntity)

        return get((UserTosId)entity.id)
    }

    @Override
    Promise<UserTos> get(UserTosId id) {
        return Promise.pure(modelMapper.toUserTos(userTosDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserTos>> search(UserTosListOptions getOption) {
        def result = []
        def entities = userTosDAO.search(getOption.userId.value, getOption)

        entities.each { UserTosEntity entity ->
            result.add(modelMapper.toUserTos(entity, new MappingContext()))
        }
        return Promise.pure(result)
    }

    @Override
    Promise<Void> delete(UserTosId id) {
        userTosDAO.delete(id.value)

        return Promise.pure(null)
    }
}
