package com.junbo.identity.service

import com.junbo.common.id.DeviceId
import com.junbo.identity.spec.v1.model.Device
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface DeviceService {
    Promise<Device> get(DeviceId id)

    Promise<Device> create(Device model)

    Promise<Device> update(Device model, Device oldModel)

    Promise<Void> delete(DeviceId id)

    Promise<Device> searchBySerialNumber(String externalRef)
}