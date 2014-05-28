/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.TosId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.data.dao.UserTosDAO
import com.junbo.identity.data.entity.user.UserTosAgreementEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Implementation for User Tos Acceptance DAO interface.
 */
@CompileStatic
class UserTosRepositorySqlImpl implements UserTosRepository {
    private UserTosDAO userTosDAO
    private ModelMapper modelMapper

    @Required
    void setUserTosDAO(UserTosDAO userTosDAO) {
        this.userTosDAO = userTosDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<UserTosAgreement> create(UserTosAgreement entity) {
        UserTosAgreementEntity userTosAgreementEntity = modelMapper.toUserTos(entity, new MappingContext())
        userTosDAO.save(userTosAgreementEntity)
        return get(new UserTosAgreementId((Long)userTosAgreementEntity.id))
    }

    @Override
    Promise<UserTosAgreement> update(UserTosAgreement entity) {
        UserTosAgreementEntity userTosAcceptanceEntity = modelMapper.toUserTos(entity, new MappingContext())
        userTosDAO.update(userTosAcceptanceEntity)

        return get((UserTosAgreementId)entity.id)
    }

    @Override
    Promise<UserTosAgreement> get(UserTosAgreementId id) {
        return Promise.pure(modelMapper.toUserTos(userTosDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserTosAgreement>> search(UserId userId) {
        def result = []
        def entities = userTosDAO.search(userId.value, new UserTosAgreementListOptions())

        entities.each { UserTosAgreementEntity entity ->
            result.add(modelMapper.toUserTos(entity, new MappingContext()))
        }
        return Promise.pure(result)
    }

    @Override
    Promise<Void> delete(UserTosAgreementId id) {
        userTosDAO.delete(id.value)

        return Promise.pure(null)
    }

    @Override
    Promise<List<UserTosAgreement>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<List<UserTosAgreement>> searchByTosId(TosId tosId, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<List<UserTosAgreement>> searchByUserIdAndTosId(UserId userId, TosId tosId, Integer limit, Integer offset) {
        return null
    }
}
