package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserEmail
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserEmailFilter extends ResourceFilterImpl<UserEmail> {
    @Override
    protected UserEmail filter(UserEmail user, MappingContext context) {
        return selfMapper.filterUserEmail(user, context)
    }

    @Override
    protected UserEmail merge(UserEmail source, UserEmail base, MappingContext context) {
        return selfMapper.mergeUserEmail(source, base, context)
    }
}