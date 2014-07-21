package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.DeviceId
import com.junbo.identity.core.service.validator.DeviceValidator
import com.junbo.identity.data.repository.DeviceRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Device
import com.junbo.identity.spec.v1.option.list.DeviceListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
class DeviceValidatorImpl implements DeviceValidator {

    private DeviceRepository deviceRepository

    private Integer deviceExternalRefMinLength
    private Integer deviceExternalRefMaxLength

    private Integer deviceDescriptionMinLength
    private Integer deviceDescriptionMaxLength

    @Override
    Promise<Device> validateForGet(DeviceId deviceId) {
        if (deviceId == null || deviceId.value == null) {
            throw new IllegalArgumentException('deviceId is null')
        }

        return deviceRepository.get(deviceId).then { Device device ->
            if (device == null) {
                throw AppErrors.INSTANCE.deviceNotFound(deviceId).exception()
            }

            return Promise.pure(device)
        }
    }

    @Override
    Promise<Void> validateForSearch(DeviceListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.externalRef == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('externalRef').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Device device) {
        checkBasicDeviceInfo(device)

        if (device.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        return deviceRepository.searchBySerialNumber(device.serialNumber).then { Device existing ->
            if (existing != null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('externalRef').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(DeviceId deviceId, Device device, Device oldDevice) {
        if (deviceId == null) {
            throw new IllegalArgumentException('deviceId is null')
        }

        if (device == null) {
            throw new IllegalArgumentException('device id snull')
        }

        if (deviceId != device.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (deviceId != oldDevice.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicDeviceInfo(device)
        if (device.serialNumber != oldDevice.serialNumber) {
            return deviceRepository.searchBySerialNumber(device.serialNumber).then { Device newDevice ->
                if (newDevice != null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('externalRef').exception()
                }
                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }

    private void checkBasicDeviceInfo(Device device) {
        if (device == null) {
            throw new IllegalArgumentException('device is null')
        }

        if (device.serialNumber == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('serialNumber').exception()
        }
        if (device.serialNumber.size() > deviceExternalRefMaxLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('serialNumber', deviceExternalRefMaxLength).exception()
        }
        if (device.serialNumber.size() < deviceExternalRefMinLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('serialNumber', deviceExternalRefMinLength).exception()
        }
    }

    @Required
    void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository
    }

    @Required
    void setDeviceExternalRefMinLength(Integer deviceExternalRefMinLength) {
        this.deviceExternalRefMinLength = deviceExternalRefMinLength
    }

    @Required
    void setDeviceExternalRefMaxLength(Integer deviceExternalRefMaxLength) {
        this.deviceExternalRefMaxLength = deviceExternalRefMaxLength
    }

    @Required
    void setDeviceDescriptionMinLength(Integer deviceDescriptionMinLength) {
        this.deviceDescriptionMinLength = deviceDescriptionMinLength
    }

    @Required
    void setDeviceDescriptionMaxLength(Integer deviceDescriptionMaxLength) {
        this.deviceDescriptionMaxLength = deviceDescriptionMaxLength
    }
}
