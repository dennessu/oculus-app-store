package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.DeviceTypeFilter
import com.junbo.identity.core.service.validator.DeviceTypeValidator
import com.junbo.identity.service.DeviceTypeService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.identity.spec.v1.option.model.DeviceTypeGetOptions
import com.junbo.identity.spec.v1.resource.DeviceTypeResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.core.Response

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class DeviceTypeResourceImpl implements DeviceTypeResource {
    @Autowired
    private DeviceTypeService deviceTypeService

    @Autowired
    private DeviceTypeFilter deviceTypeFilter

    @Autowired
    private DeviceTypeValidator deviceTypeValidator

    @Override
    Promise<DeviceType> create(DeviceType deviceType) {
        if (deviceType == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        deviceType = deviceTypeFilter.filterForCreate(deviceType)

        return deviceTypeValidator.validateForCreate(deviceType).then {
            return deviceTypeService.create(deviceType).then { DeviceType newDeviceType ->
                Created201Marker.mark(newDeviceType.id)

                newDeviceType = deviceTypeFilter.filterForGet(newDeviceType, null)
                return Promise.pure(newDeviceType)
            }
        }
    }

    @Override
    Promise<DeviceType> put(DeviceTypeId deviceTypeId, DeviceType deviceType) {
        if (deviceTypeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (deviceType == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return deviceTypeService.get(deviceTypeId).then { DeviceType oldDeviceType ->
            if (oldDeviceType == null) {
                throw AppErrors.INSTANCE.deviceTypeNotFound(deviceTypeId).exception()
            }

            deviceType = deviceTypeFilter.filterForPut(deviceType, oldDeviceType)

            return deviceTypeValidator.validateForUpdate(deviceTypeId, deviceType, oldDeviceType).then {
                return deviceTypeService.update(deviceType, oldDeviceType).then { DeviceType newDeviceType ->
                    newDeviceType = deviceTypeFilter.filterForGet(newDeviceType, null)
                    return Promise.pure(newDeviceType)
                }
            }
        }
    }

    @Override
    Promise<DeviceType> get(DeviceTypeId deviceTypeId, DeviceTypeGetOptions getOptions) {
        if (deviceTypeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return deviceTypeValidator.validateForGet(deviceTypeId).then {
            return deviceTypeService.get(deviceTypeId).then { DeviceType newDeviceType ->
                if (newDeviceType == null) {
                    throw AppErrors.INSTANCE.deviceTypeNotFound(deviceTypeId).exception()
                }

                newDeviceType = deviceTypeFilter.filterForGet(newDeviceType, null)
                return Promise.pure(newDeviceType)
            }
        }
    }

    @Override
    Promise<Results<DeviceType>> list(DeviceTypeListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return deviceTypeValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { List<DeviceType> deviceTypeList ->
                def result = new Results<DeviceType>(items: [])

                deviceTypeList.each { DeviceType newDeviceType ->
                    newDeviceType = deviceTypeFilter.filterForGet(newDeviceType, null)

                    if (newDeviceType != null) {
                        result.items.add(newDeviceType)
                    }
                }

                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<Response> delete(DeviceTypeId deviceTypeId) {
        if (deviceTypeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        return deviceTypeValidator.validateForGet(deviceTypeId).then {
            return deviceTypeService.delete(deviceTypeId).then {
                return Promise.pure(Response.status(204).build())
            }
        }
    }

    private Promise<List<DeviceType>> search(DeviceTypeListOptions listOptions) {
        return deviceTypeService.searchAll(listOptions.limit, listOptions.offset)
    }
}
