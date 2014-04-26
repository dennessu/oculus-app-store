package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class UserPersonalInfoFilter extends ResourceFilterImpl<UserPersonalInfo> {
    @Override
    protected UserPersonalInfo filter(UserPersonalInfo userPersonalInfo, MappingContext context) {
        return selfMapper.filterUserPersonalInfo(userPersonalInfo, context)
    }

    @Override
    protected UserPersonalInfo merge(UserPersonalInfo source, UserPersonalInfo base, MappingContext context) {
        return selfMapper.mergeUserPersonalInfo(source, base, context)
    }
}
