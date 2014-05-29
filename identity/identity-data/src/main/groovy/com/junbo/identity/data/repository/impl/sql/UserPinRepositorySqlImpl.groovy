/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.identity.data.dao.UserPinDAO
import com.junbo.identity.data.entity.user.UserPinEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 3/16/14.
 */
@CompileStatic
class UserPinRepositorySqlImpl implements UserPinRepository {
    private UserPinDAO userPinDAO
    private ModelMapper modelMapper

    @Required
    void setUserPinDAO(UserPinDAO userPinDAO) {
        this.userPinDAO = userPinDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<UserPin> create(UserPin entity) {
        UserPinEntity userPinEntity = modelMapper.toUserPin(entity, new MappingContext())
        userPinDAO.save(userPinEntity)

        return get(new UserPinId(userPinEntity.id))
    }

    @Override
    Promise<UserPin> update(UserPin entity) {
        UserPinEntity userPINEntity = modelMapper.toUserPin(entity, new MappingContext())
        userPinDAO.update(userPINEntity)

        return get((UserPinId)entity.id)
    }

    @Override
    Promise<UserPin> get(UserPinId id) {
        return Promise.pure(modelMapper.toUserPin(userPinDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserPin>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<List<UserPin>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<Void> delete(UserPinId id) {
        userPinDAO.delete(id.value)
        return Promise.pure(null)
    }
}
