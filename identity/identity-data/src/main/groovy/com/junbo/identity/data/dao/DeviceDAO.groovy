/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.device.DeviceEntity
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
interface DeviceDAO {
    DeviceEntity get(Long deviceId)
    DeviceEntity create(DeviceEntity device)
    DeviceEntity update(DeviceEntity device)
    DeviceEntity findIdByExternalRef(String externalRef)
}
