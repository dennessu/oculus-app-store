/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.DeviceId
import com.junbo.common.id.Id
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.DeviceFilter
import com.junbo.identity.core.service.validator.DeviceValidator
import com.junbo.identity.data.repository.DeviceRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Device
import com.junbo.identity.spec.v1.option.list.DeviceListOptions
import com.junbo.identity.spec.v1.option.model.DeviceGetOptions
import com.junbo.identity.spec.v1.resource.DeviceResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by xiali_000 on 4/8/2014.
 */
@Transactional
@CompileStatic
class DeviceResourceImpl implements DeviceResource {

    @Autowired
    private DeviceRepository deviceRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private DeviceFilter deviceFilter

    @Autowired
    private DeviceValidator deviceValidator

    @Override
    Promise<Device> create(Device device) {
        device = deviceFilter.filterForCreate(device)

        return deviceValidator.validateForCreate(device).then {
            return deviceRepository.create(device).then { Device newDevice ->
                created201Marker.mark((Id) newDevice.id)

                newDevice = deviceFilter.filterForGet(newDevice, null)
                return Promise.pure(newDevice)
            }
        }
    }

    @Override
    Promise<Device> put(DeviceId deviceId, Device device) {
        if (deviceId == null) {
            throw new IllegalArgumentException()
        }
        return deviceRepository.get(deviceId).then { Device oldDevice ->
            if (oldDevice == null) {
                throw AppErrors.INSTANCE.deviceNotFound(deviceId).exception()
            }

            device = deviceFilter.filterForPut(device, oldDevice)

            return deviceValidator.validateForUpdate(deviceId, device, oldDevice).then {
                return deviceRepository.update(device).then { Device newDevice ->
                    newDevice = deviceFilter.filterForGet(newDevice, null)
                    return Promise.pure(newDevice)
                }
            }
        }
    }

    @Override
    Promise<Device> patch(DeviceId deviceId, Device device) {
        if (deviceId == null) {
            throw new IllegalArgumentException('deviceId is null')
        }

        return deviceRepository.get(deviceId).then { Device oldDevice ->
            if (oldDevice == null) {
                throw AppErrors.INSTANCE.deviceNotFound(deviceId).exception()
            }

            device = deviceFilter.filterForPatch(device, oldDevice)

            return deviceValidator.validateForUpdate(deviceId, device, oldDevice).then {
                return deviceRepository.update(device).then { Device newDevice ->
                    newDevice = deviceFilter.filterForGet(newDevice, null)
                    return Promise.pure(newDevice)
                }
            }
        }
    }

    @Override
    Promise<Device> get(DeviceId deviceId, DeviceGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException()
        }
        return deviceValidator.validateForGet(deviceId).then {
            return deviceRepository.get(deviceId).then { Device newDevice ->
                if (newDevice == null) {
                    throw AppErrors.INSTANCE.deviceNotFound(deviceId).exception()
                }

                newDevice = deviceFilter.filterForGet(newDevice, getOptions.properties?.split(',') as List<String>)
                return Promise.pure(newDevice)
            }
        }
    }

    @Override
    Promise<Results<Device>> list(DeviceListOptions listOptions) {
        return deviceValidator.validateForSearch(listOptions).then {
            def resultList = new Results<Device>(items: [])
            return deviceRepository.searchBySerialNumber(listOptions.externalRef).then { Device newDevice ->
                if (newDevice != null) {
                    newDevice = deviceFilter.filterForGet(newDevice, listOptions.properties?.split(',') as List<String>)
                }

                if (newDevice != null) {
                    resultList.items.add(newDevice)
                }

                return Promise.pure(resultList)
            }
        }    
    }

    @Override
    Promise<Void> delete(DeviceId deviceId) {
        return deviceValidator.validateForGet(deviceId).then {
            return deviceRepository.delete(deviceId)
        }
    }
}
