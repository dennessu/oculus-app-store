package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTFAAttemptId
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/4/14.
 */
@CompileStatic
class UserTFAAttemptFilter extends ResourceFilterImpl<UserTFAAttempt> {

    @Override
    protected UserTFAAttempt filter(UserTFAAttempt userTeleAttempt, MappingContext context) {
        UserTFAAttempt result = selfMapper.filterUserTFAAttempt(userTeleAttempt, context)
        if (userTeleAttempt.userId != null && result.id != null) {
            ((UserTFAAttemptId)(result.id)).resourcePathPlaceHolder.put('userId', userTeleAttempt.userId)
        }
        return result
    }

    @Override
    protected UserTFAAttempt merge(UserTFAAttempt source, UserTFAAttempt base, MappingContext context) {
        return selfMapper.mergeUserTFAAttempt(source, base, context)
    }
}
