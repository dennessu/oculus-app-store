package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
class UserTFABackupCodeFilter extends ResourceFilterImpl<UserTFABackupCode> {

    @Override
    protected UserTFABackupCode filter(UserTFABackupCode userTFABackupCode, MappingContext context) {
        UserTFABackupCode result = selfMapper.filterUserTFABackupCode(userTFABackupCode, context)
        if (userTFABackupCode.userId != null && result.id != null) {
            ((UserTFABackupCodeId)(result.id)).resourcePathPlaceHolder.put('userId', userTFABackupCode.userId)
        }
        return result
    }

    @Override
    protected UserTFABackupCode merge(UserTFABackupCode source, UserTFABackupCode base, MappingContext context) {
        return selfMapper.mergeUserTFABackupCode(source, base, context)
    }
}
