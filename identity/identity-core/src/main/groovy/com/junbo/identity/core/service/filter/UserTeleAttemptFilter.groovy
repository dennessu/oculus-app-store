package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTeleAttemptId
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/4/14.
 */
@CompileStatic
class UserTeleAttemptFilter extends ResourceFilterImpl<UserTeleAttempt> {

    @Override
    protected UserTeleAttempt filter(UserTeleAttempt userTeleAttempt, MappingContext context) {
        UserTeleAttempt result = selfMapper.filterUserTeleAttempt(userTeleAttempt, context)
        if (userTeleAttempt.userId != null) {
            ((UserTeleAttemptId)(result.id)).resourcePathPlaceHolder.put('userId', userTeleAttempt.userId)
        }
        return result
    }

    @Override
    protected UserTeleAttempt merge(UserTeleAttempt source, UserTeleAttempt base, MappingContext context) {
        return selfMapper.mergeUserTeleAttempt(source, base, context)
    }
}
