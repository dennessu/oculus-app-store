package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTFAPhoneRepository extends BaseRepository<UserTFA, UserTFAId> {
    @ReadMethod
    Promise<Results<UserTFA>> searchTFACodeByUserIdAndPersonalInfoId(UserId userId, UserPersonalInfoId personalInfoId,
                                                               Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<UserTFA>> searchTFACodeByUserIdAndPIIAfterTime(UserId userId, UserPersonalInfoId personalInfoId,
                                                                Integer limit, Integer offset, Long startTimeOffset)
}
