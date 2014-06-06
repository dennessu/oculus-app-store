/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.CommunicationId
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.id.UserId
import com.junbo.identity.data.dao.UserCommunicationDAO
import com.junbo.identity.data.entity.user.UserCommunicationEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserCommunicationRepository
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Implementation for UserCommunicationDAO.
 */
@CompileStatic
class UserCommunicationRepositorySqlImpl implements UserCommunicationRepository {
    private UserCommunicationDAO userOptinDAO
    private ModelMapper modelMapper

    @Required
    void setUserOptinDAO(UserCommunicationDAO userOptinDAO) {
        this.userOptinDAO = userOptinDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<UserCommunication> create(UserCommunication entity) {
        UserCommunicationEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        userOptinDAO.save(userOptInEntity)

        return get(new UserCommunicationId((Long)userOptInEntity.id))
    }

    @Override
    Promise<UserCommunication> update(UserCommunication entity) {
        UserCommunicationEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        userOptinDAO.update(userOptInEntity)

        return get((UserCommunicationId)entity.id)
    }

    @Override
    Promise<UserCommunication> get(UserCommunicationId id) {
        return Promise.pure(modelMapper.toUserOptin(userOptinDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserCommunication>> search(UserOptinListOptions getOption) {
        def result = []
        def entities = []
        if (getOption.userId != null) {
            entities = userOptinDAO.searchByUserId(getOption.userId.value)
            if (getOption.communicationId != null) {
                entities.removeAll { UserCommunicationEntity entity ->
                    entity.communicationId != getOption.communicationId
                }
            }
        } else if (getOption.communicationId != null) {
            entities = userOptinDAO.searchByCommunicationId(getOption.communicationId.value)
        }

        entities.each { UserCommunicationEntity i ->
            result.add(modelMapper.toUserOptin(i, new MappingContext()))
        }
        return Promise.pure(result)
    }

    @Override
    Promise<List<UserCommunication>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<List<UserCommunication>> searchByCommunicationId(CommunicationId communicationId, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<List<UserCommunication>> searchByUserIdAndCommunicationId(UserId userId, CommunicationId communicationId, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<Void> delete(UserCommunicationId id) {
        userOptinDAO.delete(id.value)
        return Promise.pure(null)
    }
}
