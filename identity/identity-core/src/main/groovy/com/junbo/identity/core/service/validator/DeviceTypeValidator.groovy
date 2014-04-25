package com.junbo.identity.core.service.validator

import com.junbo.common.id.DeviceTypeId
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
public interface DeviceTypeValidator {
    Promise<DeviceType> validateForGet(DeviceTypeId countryId)
    Promise<Void> validateForSearch(DeviceTypeListOptions options)
    Promise<Void> validateForCreate(DeviceType country)
    Promise<Void> validateForUpdate(DeviceTypeId deviceTypeId, DeviceType deviceType, DeviceType oldDeviceType)
}