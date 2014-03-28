package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.UserTos
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
class UserTosFilter extends ResourceFilterImpl<UserTos> {
    @Override
    protected UserTos filter(UserTos userTos, MappingContext context) {
        return selfMapper.filterUserTos(userTos, context)
    }

    @Override
    protected UserTos merge(UserTos source, UserTos base, MappingContext context) {
        return selfMapper.mergeUserTos(source, base, context)
    }
}
