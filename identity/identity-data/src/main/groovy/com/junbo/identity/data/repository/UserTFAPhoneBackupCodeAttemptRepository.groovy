package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTFAPhoneBackupCodeAttemptRepository
        extends BaseRepository<UserTFABackupCodeAttempt, UserTFABackupCodeAttemptId> {
    @ReadMethod
    Promise<List<UserTFABackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)
}