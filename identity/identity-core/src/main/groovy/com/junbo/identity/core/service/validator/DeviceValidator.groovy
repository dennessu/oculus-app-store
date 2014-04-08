package com.junbo.identity.core.service.validator

import com.junbo.common.id.DeviceId
import com.junbo.identity.spec.v1.model.Device
import com.junbo.identity.spec.v1.option.list.DeviceListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
interface DeviceValidator {
    Promise<Void> validateForGet(DeviceId deviceId)
    Promise<Void> validateForSearch(DeviceListOptions options)
    Promise<Void> validateForCreate(Device device)
    Promise<Void> validateForUpdate(DeviceId deviceId, Device device, Device oldDevice)
}