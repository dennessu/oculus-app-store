/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.data.dao.UserAuthenticatorDAO
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Implementation for UserAuthenticatorDAO.
 */
@CompileStatic
class UserAuthenticatorRepositorySqlImpl implements UserAuthenticatorRepository {
    private UserAuthenticatorDAO authenticatorDAO
    private ModelMapper modelMapper

    @Required
    void setAuthenticatorDAO(UserAuthenticatorDAO authenticatorDAO) {
        this.authenticatorDAO = authenticatorDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<UserAuthenticator> create(UserAuthenticator entity) {
        UserAuthenticatorEntity userAuthenticatorEntity = modelMapper.toUserAuthenticator(entity, new MappingContext())
        authenticatorDAO.save(userAuthenticatorEntity)

        return get(new UserAuthenticatorId((Long)(userAuthenticatorEntity.id)))
    }

    @Override
    Promise<UserAuthenticator> update(UserAuthenticator entity) {
        UserAuthenticatorEntity userFederationEntity = modelMapper.toUserAuthenticator(entity, new MappingContext())
        authenticatorDAO.update(userFederationEntity)

        return get((UserAuthenticatorId)entity.id)
    }

    @Override
    Promise<UserAuthenticator> get(UserAuthenticatorId id) {
        return Promise.pure(modelMapper.toUserAuthenticator(authenticatorDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserAuthenticator>> search(AuthenticatorListOptions getOption) {
        def result = []
        if (getOption != null && getOption.userId != null) {
            def entities = authenticatorDAO.search(getOption.userId.value, getOption)

            entities.each { i ->
                result.add(modelMapper.toUserAuthenticator((UserAuthenticatorEntity)i, new MappingContext()))
            }
        }
        else if (getOption != null && getOption.value != null) {
            def entity = searchByAuthenticatorValue(getOption.value)
            if (entity != null) {
                result.add(entity)
            }
        }

        return Promise.pure(result)
    }

    private UserAuthenticator searchByAuthenticatorValue(String value) {
        def userAuthenticator = authenticatorDAO.getIdByAuthenticatorValue(value)

        return modelMapper.toUserAuthenticator(userAuthenticator, new MappingContext())
    }

    @Override
    Promise<Void> delete(UserAuthenticatorId id) {
        authenticatorDAO.delete(id.value)

        return Promise.pure(null)
    }
}