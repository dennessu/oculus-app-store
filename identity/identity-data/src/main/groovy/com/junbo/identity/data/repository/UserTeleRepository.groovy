package com.junbo.identity.data.repository

import com.junbo.common.id.UserTeleId
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleRepository extends IdentityBaseRepository<UserTeleCode, UserTeleId> {
    @ReadMethod
    Promise<UserTeleCode> findActiveTeleCode(Long userId, String phoneNumber)
}
