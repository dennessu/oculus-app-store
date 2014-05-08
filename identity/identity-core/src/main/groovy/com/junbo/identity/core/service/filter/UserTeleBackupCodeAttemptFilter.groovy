package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
class UserTeleBackupCodeAttemptFilter extends ResourceFilterImpl<UserTeleBackupCodeAttempt> {

    @Override
    protected UserTeleBackupCodeAttempt filter(UserTeleBackupCodeAttempt userTeleBackupCode, MappingContext context) {
        UserTeleBackupCodeAttempt result = selfMapper.filterUserTeleBackupCodeAttempt(userTeleBackupCode, context)
        if (userTeleBackupCode.userId != null) {
            ((UserTeleBackupCodeAttemptId)(result.id)).resourcePathPlaceHolder.put('userId', userTeleBackupCode.userId)
        }
        return result
    }

    @Override
    protected UserTeleBackupCodeAttempt merge(UserTeleBackupCodeAttempt source, UserTeleBackupCodeAttempt base,
                                              MappingContext context) {
        return selfMapper.mergeUserTeleBackupCodeAttempt(source, base, context)
    }
}
