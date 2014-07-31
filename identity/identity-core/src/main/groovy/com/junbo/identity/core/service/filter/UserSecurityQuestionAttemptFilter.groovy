package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserSecurityQuestionVerifyAttemptId
import com.junbo.identity.spec.v1.model.UserSecurityQuestionVerifyAttempt
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserSecurityQuestionAttemptFilter  extends ResourceFilterImpl<UserSecurityQuestionVerifyAttempt> {
    @Override
    protected UserSecurityQuestionVerifyAttempt filter(UserSecurityQuestionVerifyAttempt attempt,
                                                       MappingContext context) {
        UserSecurityQuestionVerifyAttempt result = selfMapper.filterUserSecurityQuestionAttempt(attempt, context)
        if (attempt.userId != null && result.id != null) {
            ((UserSecurityQuestionVerifyAttemptId)(result.id)).resourcePathPlaceHolder.put('userId', attempt.userId)
        }
        return result
    }

    @Override
    protected UserSecurityQuestionVerifyAttempt merge(UserSecurityQuestionVerifyAttempt source,
                                                      UserSecurityQuestionVerifyAttempt base, MappingContext context) {
        return selfMapper.mergeUserSecurityQuestionAttempt(source, base, context)
    }
}
