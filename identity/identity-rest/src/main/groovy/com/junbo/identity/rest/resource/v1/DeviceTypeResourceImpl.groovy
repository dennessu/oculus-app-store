package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.DeviceTypeId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.identity.spec.v1.option.model.DeviceTypeGetOptions
import com.junbo.identity.spec.v1.resource.DeviceTypeResource
import com.junbo.langur.core.promise.Promise

/**
 * Created by haomin on 14-4-25.
 */
class DeviceTypeResourceImpl implements DeviceTypeResource {
    @Override
    Promise<DeviceType> create(DeviceType deviceType) {
        return null
    }

    @Override
    Promise<DeviceType> put(DeviceTypeId deviceTypeId, DeviceType deviceType) {
        return null
    }

    @Override
    Promise<DeviceType> patch(DeviceTypeId deviceTypeId, DeviceType deviceType) {
        return null
    }

    @Override
    Promise<DeviceType> get(DeviceTypeId deviceTypeId, DeviceTypeGetOptions getOptions) {
        return null
    }

    @Override
    Promise<Results<DeviceType>> list(DeviceTypeListOptions listOptions) {
        return null
    }

    @Override
    Promise<Void> delete(DeviceTypeId deviceTypeId) {
        return null
    }
}
