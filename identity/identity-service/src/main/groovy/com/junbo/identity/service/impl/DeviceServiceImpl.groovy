package com.junbo.identity.service.impl

import com.junbo.common.id.DeviceId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.DeviceRepository
import com.junbo.identity.service.DeviceService
import com.junbo.identity.spec.v1.model.Device
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class DeviceServiceImpl implements DeviceService {
    private DeviceRepository deviceRepository

    @Override
    Promise<Device> get(DeviceId id) {
        return deviceRepository.get(id)
    }

    @Override
    Promise<Device> create(Device model) {
        return deviceRepository.create(model)
    }

    @Override
    Promise<Device> update(Device model, Device oldModel) {
        return deviceRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(DeviceId id) {
        return deviceRepository.delete(id)
    }

    @Override
    Promise<Device> searchBySerialNumber(String externalRef) {
        return deviceRepository.searchBySerialNumber(externalRef)
    }

    @Override
    Promise<Results<Device>> listAll(int limit, int offset) {
        return deviceRepository.listAll(limit, offset)
    }

    @Required
    void setDeviceRepository(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository
    }
}
