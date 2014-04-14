package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPasswordFilter extends ResourceFilterImpl<UserPassword> {
    @Override
    protected UserPassword filter(UserPassword userPassword, MappingContext context) {
        return selfMapper.filterUserPassword(userPassword, context)
    }

    @Override
    protected UserPassword merge(UserPassword source, UserPassword base, MappingContext context) {
        return selfMapper.mergeUserPassword(source, base, context)
    }
}
