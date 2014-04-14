package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserPhoneNumber
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPhoneNumberFilter  extends ResourceFilterImpl<UserPhoneNumber> {
    @Override
    protected UserPhoneNumber filter(UserPhoneNumber user, MappingContext context) {
        return selfMapper.filterUserPhoneNumber(user, context)
    }

    @Override
    protected UserPhoneNumber merge(UserPhoneNumber source, UserPhoneNumber base, MappingContext context) {
        return selfMapper.mergeUserPhoneNumber(source, base, context)
    }
}
