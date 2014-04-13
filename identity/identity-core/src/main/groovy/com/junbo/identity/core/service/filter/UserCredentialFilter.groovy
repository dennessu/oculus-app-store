package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
class UserCredentialFilter extends ResourceFilterImpl<UserCredential>  {
    @Override
    protected UserCredential filter(UserCredential userCredential, MappingContext context) {
        return selfMapper.filterUserCredential(userCredential, context)
    }

    @Override
    protected UserCredential merge(UserCredential source, UserCredential base, MappingContext context) {
        return selfMapper.mergeUserCredential(source, base, context)
    }
}
