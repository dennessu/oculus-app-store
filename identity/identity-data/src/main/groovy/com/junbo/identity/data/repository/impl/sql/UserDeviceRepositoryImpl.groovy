/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserDeviceId
import com.junbo.identity.data.dao.UserDeviceDAO
import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserDeviceRepository
import com.junbo.identity.spec.v1.model.UserDevice
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Implementation for UserDeviceDAO.
 */
@CompileStatic
class UserDeviceRepositoryImpl implements UserDeviceRepository {
    @Autowired
    private UserDeviceDAO userDeviceDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<UserDevice> create(UserDevice entity) {
        UserDeviceEntity userDeviceProfileEntity = modelMapper.toUserDevice(entity, new MappingContext())
        userDeviceDAO.save(userDeviceProfileEntity)

        return get(new UserDeviceId((Long)userDeviceProfileEntity.id))
    }

    @Override
    Promise<UserDevice> update(UserDevice entity) {
        UserDeviceEntity userDeviceEntity = modelMapper.toUserDevice(entity, new MappingContext())
        userDeviceDAO.update(userDeviceEntity)

        return get((UserDeviceId)entity.id)
    }

    @Override
    Promise<UserDevice> get(UserDeviceId id) {
        return Promise.pure(modelMapper.toUserDevice(userDeviceDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<UserDevice>> search(UserDeviceListOptions getOption) {
        def result = []
        def entities = []
        if (getOption.userId != null) {
            entities = userDeviceDAO.search(getOption.userId.value, getOption)

        } else if (getOption.deviceId != null) {
            entities = userDeviceDAO.findByDeviceId(getOption.deviceId.value, getOption)
        }
        if (entities == null) {
            return Promise.pure(null)
        }
        entities.each { UserDeviceEntity entity ->
            result.add(modelMapper.toUserDevice(entity, new MappingContext()))
        }
        return Promise.pure(result)
    }

    @Override
    Promise<Void> delete(UserDeviceId id) {
        userDeviceDAO.delete(id.value)
        return Promise.pure(null)
    }
}
