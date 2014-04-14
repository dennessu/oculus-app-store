/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl

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
import org.springframework.beans.factory.annotation.Autowired

/**
 * Implementation for User Tos Acceptance DAO interface.
 */
@CompileStatic
class UserTosRepositoryImpl implements UserTosRepository {
    @Autowired
    private UserTosDAO userTosDAO

    @Autowired
    private ModelMapper modelMapper

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
    Promise<List<UserTosAgreement>> search(UserTosAgreementListOptions getOption) {
        def result = []
        def entities = userTosDAO.search(getOption.userId.value, getOption)

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
}
