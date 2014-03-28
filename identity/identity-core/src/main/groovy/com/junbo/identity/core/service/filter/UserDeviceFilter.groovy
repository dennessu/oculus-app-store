package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.UserDevice
import com.junbo.oom.core.MappingContext

/**
 * Created by liangfu on 3/27/14.
 */
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
