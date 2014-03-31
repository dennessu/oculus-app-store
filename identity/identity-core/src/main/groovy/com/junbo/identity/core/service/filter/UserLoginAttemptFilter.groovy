package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.UserLoginAttempt
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
class UserLoginAttemptFilter extends ResourceFilterImpl<UserLoginAttempt> {
    @Override
    protected UserLoginAttempt filter(UserLoginAttempt userLoginAttempt, MappingContext context) {
        return selfMapper.filterUserLoginAttempt(userLoginAttempt, context)
    }

    @Override
    protected UserLoginAttempt merge(UserLoginAttempt source, UserLoginAttempt base, MappingContext context) {
        return selfMapper.mergeUserLoginAttempt(source, base, context)
    }
}
