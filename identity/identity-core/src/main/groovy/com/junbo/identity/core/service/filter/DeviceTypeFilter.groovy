package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class DeviceTypeFilter extends ResourceFilterImpl<DeviceType> {
    @Override
    protected DeviceType filter(DeviceType model, MappingContext context) {
        return selfMapper.filterDeviceType(model, context)
    }

    @Override
    protected DeviceType merge(DeviceType source, DeviceType base, MappingContext context) {
        return selfMapper.mergeDeviceType(source, base, context)
    }
}
