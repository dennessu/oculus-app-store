/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.DeviceId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Device
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
interface DeviceRepository extends BaseRepository<Device, DeviceId> {
    @ReadMethod
    Promise<Device> searchBySerialNumber(String externalRef)

    @ReadMethod
    Promise<Results<Device>> listAll(int limit, int offset)
}