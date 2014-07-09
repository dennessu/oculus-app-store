package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.DeviceTypeFilter
import com.junbo.identity.core.service.validator.DeviceTypeValidator
import com.junbo.identity.data.repository.DeviceTypeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.identity.spec.v1.option.model.DeviceTypeGetOptions
import com.junbo.identity.spec.v1.resource.DeviceTypeResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class DeviceTypeResourceImpl implements DeviceTypeResource {
    private static final String IDENTITY_ADMIN_SCOPE = 'identity.admin'

    @Autowired
    private DeviceTypeRepository deviceTypeRepository

    @Autowired
    private DeviceTypeFilter deviceTypeFilter

    @Autowired
    private DeviceTypeValidator deviceTypeValidator

    @Override
    Promise<DeviceType> create(DeviceType deviceType) {
        if (deviceType == null) {
            throw new IllegalArgumentException('deviceType is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }

        deviceType = deviceTypeFilter.filterForCreate(deviceType)

        return deviceTypeValidator.validateForCreate(deviceType).then {
            return deviceTypeRepository.create(deviceType).then { DeviceType newDeviceType ->
                Created201Marker.mark(newDeviceType.id)

                newDeviceType = deviceTypeFilter.filterForGet(newDeviceType, null)
                return Promise.pure(newDeviceType)
            }
        }
    }

    @Override
    Promise<DeviceType> put(DeviceTypeId deviceTypeId, DeviceType deviceType) {
        if (deviceTypeId == null) {
            throw new IllegalArgumentException('deviceTypeId is null')
        }

        if (deviceType == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }

        return deviceTypeRepository.get(deviceTypeId).then { DeviceType oldDeviceType ->
            if (oldDeviceType == null) {
                throw AppErrors.INSTANCE.deviceTypeNotFound(deviceTypeId).exception()
            }

            deviceType = deviceTypeFilter.filterForPut(deviceType, oldDeviceType)

            return deviceTypeValidator.validateForUpdate(deviceTypeId, deviceType, oldDeviceType).then {
                return deviceTypeRepository.update(deviceType).then { DeviceType newDeviceType ->
                    newDeviceType = deviceTypeFilter.filterForGet(newDeviceType, null)
                    return Promise.pure(newDeviceType)
                }
            }
        }
    }

    @Override
    Promise<DeviceType> patch(DeviceTypeId deviceTypeId, DeviceType deviceType) {
        if (deviceTypeId == null) {
            throw new IllegalArgumentException('deviceTypeId is null')
        }

        if (deviceType == null) {
            throw new IllegalArgumentException('deviceType is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }

        return deviceTypeRepository.get(deviceTypeId).then { DeviceType oldDeviceType ->
            if (oldDeviceType == null) {
                throw AppErrors.INSTANCE.deviceTypeNotFound(deviceTypeId).exception()
            }

            deviceType = deviceTypeFilter.filterForPatch(deviceType, oldDeviceType)

            return deviceTypeValidator.validateForUpdate(
                    deviceTypeId, deviceType, oldDeviceType).then {
                return deviceTypeRepository.update(deviceType).then { DeviceType newDeviceType ->
                    newDeviceType = deviceTypeFilter.filterForGet(newDeviceType, null)
                    return Promise.pure(newDeviceType)
                }
            }
        }
    }

    @Override
    Promise<DeviceType> get(DeviceTypeId deviceTypeId, DeviceTypeGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return deviceTypeValidator.validateForGet(deviceTypeId).then {
            return deviceTypeRepository.get(deviceTypeId).then { DeviceType newDeviceType ->
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
    Promise<Void> delete(DeviceTypeId deviceTypeId) {
        if (deviceTypeId == null) {
            throw new IllegalArgumentException('countryId is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }

        return deviceTypeValidator.validateForGet(deviceTypeId).then {
            return deviceTypeRepository.delete(deviceTypeId)
        }
    }

    private Promise<List<DeviceType>> search(DeviceTypeListOptions listOptions) {
        return deviceTypeRepository.searchAll(listOptions.limit, listOptions.offset)
    }
}
