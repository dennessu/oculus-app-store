package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.Device
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
class DeviceFilter extends ResourceFilterImpl<Device> {
    @Override
    protected Device filter(Device device, MappingContext context) {
        return selfMapper.filterDevice(device, context)
    }

    @Override
    protected Device merge(Device source, Device base, MappingContext context) {
        return selfMapper.mergeDevice(source, base, context)
    }
}
