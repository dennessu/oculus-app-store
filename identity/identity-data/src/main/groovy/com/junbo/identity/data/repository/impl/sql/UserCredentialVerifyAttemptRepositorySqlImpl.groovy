/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.data.dao.UserCredentialVerifyAttemptDAO
import com.junbo.identity.data.entity.user.UserCredentialVerifyAttemptEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserCredentialVerifyAttemptRepository
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserCredentialVerifyAttemptRepositorySqlImpl implements UserCredentialVerifyAttemptRepository {
    private UserCredentialVerifyAttemptDAO credentialVerifyAttemptDAO
    private ModelMapper modelMapper

    @Required
    void setCredentialVerifyAttemptDAO(UserCredentialVerifyAttemptDAO credentialVerifyAttemptDAO) {
        this.credentialVerifyAttemptDAO = credentialVerifyAttemptDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt entity) {
        UserCredentialVerifyAttemptEntity userLoginAttemptEntity =
                modelMapper.toUserCredentialVerifyAttempt(entity, new MappingContext())
        credentialVerifyAttemptDAO.save(userLoginAttemptEntity)

        return get(new UserCredentialVerifyAttemptId(userLoginAttemptEntity.id))
    }

    @Override
    Promise<UserCredentialVerifyAttempt> update(UserCredentialVerifyAttempt entity) {
        UserCredentialVerifyAttemptEntity userLoginAttemptEntity =
                modelMapper.toUserCredentialVerifyAttempt(entity, new MappingContext())
        credentialVerifyAttemptDAO.update(userLoginAttemptEntity)

        return get((UserCredentialVerifyAttemptId)entity.id)
    }

    @Override
    Promise<UserCredentialVerifyAttempt> get(UserCredentialVerifyAttemptId id) {
        return Promise.pure(modelMapper.toUserCredentialVerifyAttempt(
                credentialVerifyAttemptDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByUserIdAndCredentialType(UserId userId, String type, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<Void> delete(UserCredentialVerifyAttemptId id) {
        credentialVerifyAttemptDAO.delete(id.value)
        return Promise.pure(null)
    }
}
