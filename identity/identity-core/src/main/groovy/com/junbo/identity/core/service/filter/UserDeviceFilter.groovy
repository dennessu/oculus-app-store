package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserDevice
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/27/14.
 */
@CompileStatic
class UserDeviceFilter extends ResourceFilterImpl<UserDevice> {
    @Override
    protected UserDevice filter(UserDevice userDevice, MappingContext context) {
        return selfMapper.filterUserDevice(userDevice, context)
    }

    @Override
    protected UserDevice merge(UserDevice source, UserDevice base, MappingContext context) {
        return selfMapper.mergeUserDevice(source, base, context)
    }
}
