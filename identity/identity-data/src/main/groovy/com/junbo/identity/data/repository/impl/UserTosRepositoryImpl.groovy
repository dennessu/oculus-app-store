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
import com.junbo.identity.spec.options.UserTosListOptions
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Implementation for User Tos Acceptance DAO interface.
 */
class UserTosRepositoryImpl implements UserTosRepository {
    @Autowired
    @Qualifier('userTosDAO')
    private UserTosDAO userTosDAO

    @Autowired
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Override
    UserTos save(UserTos entity) {
        UserTosEntity userTosAcceptanceEntity = modelMapper.toUserTos(entity, new MappingContext())
        userTosDAO.save(userTosAcceptanceEntity)
        return get(new UserTosId(userTosAcceptanceEntity.id))
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
    List<UserTos> search(UserTosListOptions getOption) {
        return new ArrayList<UserTos>()
        /*
        def result = []
        def entities = userTosDAO.search(getOption.userId.value, getOption)

        entities.flatten { i ->
            result.add(modelMapper.toUserTos(i, new MappingContext()))
        }
        return result
        */
    }

    @Override
    void delete(UserTosId id) {
        userTosDAO.delete(id.value)
    }
}
