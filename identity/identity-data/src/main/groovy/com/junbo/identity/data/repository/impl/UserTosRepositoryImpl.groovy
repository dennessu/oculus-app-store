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
import com.junbo.identity.spec.model.options.UserTosGetOption
import com.junbo.identity.spec.model.users.UserTos
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired

/**
 * Implementation for User Tos Acceptance DAO interface.
 */
class UserTosRepositoryImpl implements UserTosRepository {
    @Autowired
    private UserTosDAO userTosDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    UserTos save(UserTos entity) {
        UserTosEntity userTosAcceptanceEntity = modelMapper.toUserTos(entity, new MappingContext())
        userTosDAO.save(userTosAcceptanceEntity)
        return get(entity.id)
    }

    @Override
    UserTos update(UserTos entity) {
        UserTosEntity userTosAcceptanceEntity = modelMapper.toUserTos(entity, new MappingContext())
        userTosDAO.update(userTosAcceptanceEntity)

        return get(entity.id)
    }

    @Override
    UserTos get(UserTosId id) {
        return modelMapper.toUserTos(userTosDAO.get(id.value), new MappingContext())
    }

    @Override
    List<UserTos> search(UserTosGetOption getOption) {
        def result = []
        def entities = userTosDAO.search(getOption)

        entities.flatten { i ->
            result.add(modelMapper.toUserTos(i, new MappingContext()))
        }
        return result
    }

    @Override
    void delete(UserTosId id) {
        userTosDAO.delete(id.value)
    }
}
