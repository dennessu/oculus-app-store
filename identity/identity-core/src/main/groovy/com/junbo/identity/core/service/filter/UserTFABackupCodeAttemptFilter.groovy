package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
class UserTFABackupCodeAttemptFilter extends ResourceFilterImpl<UserTFABackupCodeAttempt> {

    @Override
    protected UserTFABackupCodeAttempt filter(UserTFABackupCodeAttempt userTFABackupCode, MappingContext context) {
        UserTFABackupCodeAttempt result = selfMapper.filterUserTFABackupCodeAttempt(userTFABackupCode, context)
        if (userTFABackupCode.userId != null && result.id != null) {
            ((UserTFABackupCodeAttemptId)(result.id)).resourcePathPlaceHolder.put('userId', userTFABackupCode.userId)
        }
        return result
    }

    @Override
    protected UserTFABackupCodeAttempt merge(UserTFABackupCodeAttempt source, UserTFABackupCodeAttempt base,
                                              MappingContext context) {
        return selfMapper.mergeUserTFABackupCodeAttempt(source, base, context)
    }
}
