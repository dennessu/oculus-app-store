package com.junbo.identity.core.service.validator.impl

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
            throw AppErrors.INSTANCE.parameterRequired('externalRef').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Device device) {
        checkBasicDeviceInfo(device)

        if (device.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return deviceRepository.searchByExternalRef(device.externalRef).then { Device existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldInvalid('externalRef').exception()
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
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (deviceId != oldDevice.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicDeviceInfo(device)
        if (device.externalRef != oldDevice.externalRef) {
            return deviceRepository.searchByExternalRef(device.externalRef).then { Device newDevice ->
                if (newDevice != null) {
                    throw AppErrors.INSTANCE.fieldInvalid('externalRef').exception()
                }
                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }

    private void checkBasicDeviceInfo(Device device) {
        if (device == null) {
            throw new IllegalArgumentException('device id null')
        }

        if (device.externalRef == null) {
            throw AppErrors.INSTANCE.fieldRequired('externalRef').exception()
        }
        if (device.externalRef.size() > deviceExternalRefMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('externalRef', deviceExternalRefMaxLength).exception()
        }
        if (device.externalRef.size() < deviceExternalRefMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('externalRef', deviceExternalRefMinLength).exception()
        }

        if (device.description == null) {
            throw AppErrors.INSTANCE.fieldRequired('description').exception()
        }
        if (device.description.size() > deviceDescriptionMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('description', deviceDescriptionMaxLength).exception()
        }
        if (device.description.size() < deviceDescriptionMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('description', deviceDescriptionMinLength).exception()
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
