package com.junbo.identity.core.service.filter
import com.junbo.identity.spec.model.users.UserOptin
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserOptinFilter extends ResourceFilterImpl<UserOptin> {
    @Override
    protected UserOptin filter(UserOptin userOptin, MappingContext context) {
        return selfMapper.filterUserOptin(userOptin, context)
    }

    @Override
    protected UserOptin merge(UserOptin source, UserOptin base, MappingContext context) {
        return selfMapper.mergeUserOptin(source, base, context)
    }
}
