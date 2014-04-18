/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserOptinId
import com.junbo.identity.data.dao.UserOptinDAO
import com.junbo.identity.data.entity.user.UserOptinEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserOptinRepository
import com.junbo.identity.spec.v1.model.UserOptin
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Implementation for UserOptinDAO.
 */
@CompileStatic
class UserOptinRepositoryImpl implements UserOptinRepository {
    @Autowired
    private UserOptinDAO userOptinDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserOptin> create(UserOptin entity) {
        UserOptinEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        userOptinDAO.save(userOptInEntity)

        return get(new UserOptinId((Long)userOptInEntity.id))
    }

    @Override
    Promise<UserOptin> update(UserOptin entity) {
        UserOptinEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        userOptinDAO.update(userOptInEntity)

        return get((UserOptinId)entity.id)
    }

    @Override
    Promise<UserOptin> get(UserOptinId id) {
        return Promise.pure(modelMapper.toUserOptin(userOptinDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserOptin>> search(UserOptinListOptions getOption) {
        def result = []
        def entities = []
        // todo:    Need to discuss with kevin about paging in view query
        if (getOption.userId != null) {
            entities = userOptinDAO.searchByUserId(getOption.userId.value)
            if (getOption.type != null) {
                entities.removeAll { UserOptinEntity entity ->
                    entity.type != getOption.type
                }
            }
        } else if (getOption.type != null) {
            entities = userOptinDAO.searchByType(getOption.type)
        }

        entities.each { UserOptinEntity i ->
            result.add(modelMapper.toUserOptin(i, new MappingContext()))
        }
        return Promise.pure(result)
    }

    @Override
    Promise<Void> delete(UserOptinId id) {
        userOptinDAO.delete(id.value)
        return Promise.pure(null)
    }
}
