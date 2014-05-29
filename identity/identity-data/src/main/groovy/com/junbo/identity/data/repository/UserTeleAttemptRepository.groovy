package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleAttemptId
import com.junbo.common.id.UserTeleId
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleAttemptRepository extends BaseRepository<UserTeleAttempt, UserTeleAttemptId> {
    @ReadMethod
    Promise<List<UserTeleAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserTeleAttempt>> searchByUserIdAndUserTeleId(UserId userId, UserTeleId userTeleId,
                                                               Integer limit, Integer offset)
}
