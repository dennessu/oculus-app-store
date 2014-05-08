package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
class UserTeleBackupCodeFilter extends ResourceFilterImpl<UserTeleBackupCode> {

    @Override
    protected UserTeleBackupCode filter(UserTeleBackupCode userTeleBackupCode, MappingContext context) {
        UserTeleBackupCode result = selfMapper.filterUserTeleBackupCode(userTeleBackupCode, context)
        if (userTeleBackupCode.userId != null) {
            ((UserTeleBackupCodeId)(result.id)).resourcePathPlaceHolder.put('userId', userTeleBackupCode.userId)
        }
        return result
    }

    @Override
    protected UserTeleBackupCode merge(UserTeleBackupCode source, UserTeleBackupCode base, MappingContext context) {
        return selfMapper.mergeUserTeleBackupCode(source, base, context)
    }
}
