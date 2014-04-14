package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
class UserCredentialVerifyAttemptFilter extends ResourceFilterImpl<UserCredentialVerifyAttempt> {
    @Override
    protected UserCredentialVerifyAttempt filter(UserCredentialVerifyAttempt userLoginAttempt, MappingContext context) {
        return selfMapper.filterUserCredentialVerifyAttempt(userLoginAttempt, context)
    }

    @Override
    protected UserCredentialVerifyAttempt merge(UserCredentialVerifyAttempt source,
                                                UserCredentialVerifyAttempt base, MappingContext context) {
        return selfMapper.mergeUserCredentialVerifyAttempt(source, base, context)
    }
}
