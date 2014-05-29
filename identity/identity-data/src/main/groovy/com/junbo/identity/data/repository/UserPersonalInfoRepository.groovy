package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/25/14.
 */
@CompileStatic
interface UserPersonalInfoRepository extends BaseRepository<UserPersonalInfo, UserPersonalInfoId> {
    @ReadMethod
    Promise<List<UserPersonalInfo>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserPersonalInfo>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserPersonalInfo>> searchByEmail(String email, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserPersonalInfo>> searchByPhoneNumber(String phoneNumber, Integer limit, Integer offset)
}
