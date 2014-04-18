/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.identity.data.dao.UserCredentialVerifyAttemptDAO
import com.junbo.identity.data.entity.user.UserCredentialVerifyAttemptEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserCredentialVerifyAttemptRepository
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserCredentialVerifyAttemptRepositorySqlImpl implements UserCredentialVerifyAttemptRepository {
    @Autowired
    private UserCredentialVerifyAttemptDAO credentialVerifyAttemptDAO

    @Autowired
    private ModelMapper modelMapper

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
    Promise<List<UserCredentialVerifyAttempt>> search(UserCredentialAttemptListOptions getOption) {
        List entities = credentialVerifyAttemptDAO.search(getOption.userId.value, getOption)

        List<UserCredentialVerifyAttempt> results = new ArrayList<UserCredentialVerifyAttempt>()
        entities.each { UserCredentialVerifyAttemptEntity entity ->
            results.add(modelMapper.toUserCredentialVerifyAttempt(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(UserCredentialVerifyAttemptId id) {
        credentialVerifyAttemptDAO.delete(id.value)
        return Promise.pure(null)
    }
}
