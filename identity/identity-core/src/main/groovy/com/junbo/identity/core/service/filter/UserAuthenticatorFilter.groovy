package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.UserAuthenticator
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Created by liangfu on 3/27/14.
 */
@Component
@CompileStatic
class UserAuthenticatorFilter extends ResourceFilterImpl<UserAuthenticator> {
    @Override
    protected UserAuthenticator filter(UserAuthenticator userAuthenticator, MappingContext context) {
        return selfMapper.filterUserAuthenticator(userAuthenticator, context)
    }

    @Override
    protected UserAuthenticator merge(UserAuthenticator source, UserAuthenticator base, MappingContext context) {
        return selfMapper.mergeUserAuthenticator(source, base, context)
    }
}
