/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.DeviceId
import com.junbo.identity.data.dao.DeviceDAO
import com.junbo.identity.data.entity.device.DeviceEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.DeviceRepository
import com.junbo.identity.spec.v1.model.Device
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
class DeviceRepositorySqlImpl implements DeviceRepository {

    @Autowired
    private DeviceDAO deviceDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<Device> get(DeviceId groupId) {
        DeviceEntity deviceEntity = deviceDAO.get(groupId.value)

        if (deviceEntity == null) {
            return Promise.pure(null)
        }
        return Promise.pure(modelMapper.toDevice(deviceEntity, new MappingContext()))
    }

    @Override
    Promise<Device> create(Device device) {
        DeviceEntity entity = modelMapper.toDevice(device, new MappingContext())
        entity = deviceDAO.create(entity)

        return get(new DeviceId((Long)entity.id))
    }

    @Override
    Promise<Device> update(Device device) {
        DeviceEntity entity = modelMapper.toDevice(device, new MappingContext())
        deviceDAO.update(entity)

        return get((DeviceId)device.id)
    }

    @Override
    Promise<Device> searchByExternalRef(String externalRef) {
        DeviceEntity entity = deviceDAO.findIdByExternalRef(externalRef)
        if (entity == null) {
            return Promise.pure(null)
        }
        return get(new DeviceId((Long)entity.id))
    }
}
