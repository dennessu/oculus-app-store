package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.identity.core.service.validator.DeviceTypeValidator
import com.junbo.identity.data.repository.DeviceTypeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class DeviceTypeValidatorImpl implements DeviceTypeValidator {

    private DeviceTypeRepository deviceTypeRepository

    @Required
    void setDeviceTypeRepository(DeviceTypeRepository deviceTypeRepository) {
        this.deviceTypeRepository = deviceTypeRepository
    }

    @Override
    Promise<DeviceType> validateForGet(DeviceTypeId deviceTypeId) {
        if (deviceTypeId == null || deviceTypeId.value == null) {
            throw new IllegalArgumentException('deviceTypeId is null')
        }

        return deviceTypeRepository.get(deviceTypeId).then { DeviceType deviceType ->
            if (deviceType == null) {
                throw AppErrors.INSTANCE.deviceTypeNotFound(deviceTypeId).exception()
            }

            return Promise.pure(deviceType)
        }
    }

    @Override
    Promise<Void> validateForSearch(DeviceTypeListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(DeviceType deviceType) {
        if (deviceType.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForUpdate(DeviceTypeId deviceTypeId, DeviceType deviceType, DeviceType oldDeviceType) {
        if (deviceTypeId == null) {
            throw new IllegalArgumentException('deviceTypeId is null')
        }

        if (deviceType == null) {
            throw new IllegalArgumentException('deviceType is null')
        }

        if (deviceTypeId != deviceType.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (deviceTypeId != oldDeviceType.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        return Promise.pure(null)
    }
}
