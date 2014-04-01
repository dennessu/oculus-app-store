package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserSecurityQuestionAttemptFilter  extends ResourceFilterImpl<UserSecurityQuestionAttempt> {
    @Override
    protected UserSecurityQuestionAttempt filter(UserSecurityQuestionAttempt attempt, MappingContext context) {
        return selfMapper.filterUserSecurityQuestionAttempt(attempt, context)
    }

    @Override
    protected UserSecurityQuestionAttempt merge(UserSecurityQuestionAttempt source,
                                                UserSecurityQuestionAttempt base, MappingContext context) {
        return selfMapper.mergeUserSecurityQuestionAttempt(source, base, context)
    }
}
