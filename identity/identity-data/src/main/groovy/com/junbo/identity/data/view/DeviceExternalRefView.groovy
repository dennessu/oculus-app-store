/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.view

import com.junbo.identity.data.entity.device.DeviceEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class DeviceExternalRefView implements EntityView<Long, DeviceEntity, String> {

    @Override
    String getName() {
        return 'device_deviceExternalRef'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<DeviceEntity> getEntityType() {
        return DeviceEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(DeviceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.externalRef != null
    }

    @Override
    List<String> mapEntity(DeviceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.externalRef]
    }
}
