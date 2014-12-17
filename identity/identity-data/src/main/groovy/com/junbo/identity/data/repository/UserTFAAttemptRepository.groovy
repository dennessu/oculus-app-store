package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.common.id.UserTFAId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTFAAttemptRepository extends BaseRepository<UserTFAAttempt, UserTFAAttemptId> {
    @ReadMethod
    Promise<Results<UserTFAAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<UserTFAAttempt>> searchByUserIdAndUserTFAId(UserId userId, UserTFAId userTFAId,
                                                               Integer limit, Integer offset)
}
