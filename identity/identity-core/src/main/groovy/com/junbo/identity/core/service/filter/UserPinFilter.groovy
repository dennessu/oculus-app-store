package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.UserPin
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPinFilter extends ResourceFilterImpl<UserPin> {
    @Override
    protected UserPin filter(UserPin userPin, MappingContext context) {
        return selfMapper.filterUserPin(userPin, context)
    }

    @Override
    protected UserPin merge(UserPin source, UserPin base, MappingContext context) {
        return selfMapper.mergeUserPin(source, base, context)
    }
}
