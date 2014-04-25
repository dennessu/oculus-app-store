package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.DeviceTypeId
import com.junbo.identity.core.service.validator.DeviceTypeValidator
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class DeviceTypeValidatorImpl implements DeviceTypeValidator {
    @Override
    Promise<DeviceType> validateForGet(DeviceTypeId countryId) {
        return null
    }

    @Override
    Promise<Void> validateForSearch(DeviceTypeListOptions options) {
        return null
    }

    @Override
    Promise<Void> validateForCreate(DeviceType country) {
        return null
    }

    @Override
    Promise<Void> validateForUpdate(DeviceTypeId deviceTypeId, DeviceType deviceType, DeviceType oldDeviceType) {
        return null
    }
}
