/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.data.dao.UserAuthenticatorDAO
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption
import com.junbo.identity.spec.model.users.UserAuthenticator
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Implementation for UserAuthenticatorDAO.
 */
class UserAuthenticatorRepositoryImpl implements UserAuthenticatorRepository {
    @Autowired
    @Qualifier('authenticatorDAO')
    private UserAuthenticatorDAO authenticatorDAO

    @Autowired
    @Qualifier("modelMapperImpl")
    private ModelMapper modelMapper

    @Override
    UserAuthenticator save(UserAuthenticator entity) {
        UserAuthenticatorEntity userAuthenticatorEntity = modelMapper.toUserAuthenticator(entity, new MappingContext())
        authenticatorDAO.save(userAuthenticatorEntity)

        return get(entity.id)
    }

    @Override
    UserAuthenticator update(UserAuthenticator entity) {
        UserAuthenticatorEntity userFederationEntity = modelMapper.toUserAuthenticator(entity, new MappingContext())
        authenticatorDAO.update(userFederationEntity)

        return get(entity.id)
    }

    @Override
    UserAuthenticator get(UserAuthenticatorId id) {
        return modelMapper.toUserAuthenticator(authenticatorDAO.get(id.value), new MappingContext())
    }

    @Override
    List<UserAuthenticator> search(UserAuthenticatorGetOption getOption) {
        def result = []
        def entities = authenticatorDAO.search(getOption)

        entities.flatten { i ->
            result.add(modelMapper.toUserAuthenticator(i, new MappingContext()))
        }
        return result
    }

    @Override
    void delete(UserAuthenticatorId id) {
        authenticatorDAO.delete(id.value)
    }
}
