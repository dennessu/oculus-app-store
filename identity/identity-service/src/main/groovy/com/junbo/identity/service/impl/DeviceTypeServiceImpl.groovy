package com.junbo.identity.service.impl

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.DeviceTypeRepository
import com.junbo.identity.service.DeviceTypeService
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class DeviceTypeServiceImpl implements DeviceTypeService {

    private DeviceTypeRepository deviceTypeRepository

    @Override
    Promise<DeviceType> get(DeviceTypeId id) {
        return deviceTypeRepository.get(id)
    }

    @Override
    Promise<DeviceType> create(DeviceType model) {
        return deviceTypeRepository.create(model)
    }

    @Override
    Promise<DeviceType> update(DeviceType model, DeviceType oldModel) {
        return deviceTypeRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(DeviceTypeId id) {
        return deviceTypeRepository.delete(id)
    }

    @Override
    Promise<Results<DeviceType>> searchAll(Integer limit, Integer offset) {
        return deviceTypeRepository.searchAll(limit, offset)
    }

    @Override
    Promise<Results<DeviceType>> searchByDeviceTypeCode(String typeCode, Integer limit, Integer offset) {
        return deviceTypeRepository.searchByDeviceTypeCode(typeCode, limit, offset)
    }

    @Required
    void setDeviceTypeRepository(DeviceTypeRepository deviceTypeRepository) {
        this.deviceTypeRepository = deviceTypeRepository
    }
}
