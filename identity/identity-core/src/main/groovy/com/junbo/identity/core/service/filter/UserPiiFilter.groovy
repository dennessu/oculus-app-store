package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
class UserPiiFilter extends ResourceFilterImpl<UserPii> {
    @Override
    protected UserPii filter(UserPii userPii, MappingContext context) {
        return selfMapper.filterUserPii(userPii, context)
    }

    @Override
    protected UserPii merge(UserPii source, UserPii base, MappingContext context) {
        return selfMapper.mergeUserPii(source, base, context)
    }
}
