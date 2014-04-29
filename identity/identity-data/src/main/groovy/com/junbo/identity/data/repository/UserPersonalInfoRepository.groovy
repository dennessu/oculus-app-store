package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/25/14.
 */
@CompileStatic
interface UserPersonalInfoRepository extends IdentityBaseRepository<UserPersonalInfo, UserPersonalInfoId> {
    @ReadMethod
    Promise<List<UserPersonalInfo>> searchByUserId(UserId userId)

    @ReadMethod
    Promise<List<UserPersonalInfo>> searchByUserIdAndType(UserId userId, String type)
}
