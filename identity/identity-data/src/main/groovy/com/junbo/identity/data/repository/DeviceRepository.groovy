/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.DeviceId
import com.junbo.identity.spec.v1.model.Device
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
interface DeviceRepository extends IdentityBaseRepository<Device, DeviceId> {
    @ReadMethod
    Promise<Device> searchBySerialNumber(String externalRef)
}