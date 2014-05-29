package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleBackupCodeAttemptRepository
        extends BaseRepository<UserTeleBackupCodeAttempt, UserTeleBackupCodeAttemptId> {
    @ReadMethod
    Promise<List<UserTeleBackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)
}