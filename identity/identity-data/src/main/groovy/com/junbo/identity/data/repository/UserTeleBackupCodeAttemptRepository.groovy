package com.junbo.identity.data.repository

import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleBackupCodeAttemptRepository
        extends IdentityBaseRepository<UserTeleBackupCodeAttempt, UserTeleBackupCodeAttemptId> {
    @ReadMethod
    Promise<List<UserTeleBackupCodeAttempt>> search(UserTeleBackupCodeAttemptListOptions listOptions)
}