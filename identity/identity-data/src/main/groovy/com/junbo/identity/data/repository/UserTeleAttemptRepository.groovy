package com.junbo.identity.data.repository

import com.junbo.common.id.UserTeleAttemptId
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleAttemptRepository extends IdentityBaseRepository<UserTeleAttempt, UserTeleAttemptId> {

    @ReadMethod
    Promise<List<UserTeleAttempt>> search(UserTeleAttemptListOptions listOptions)
}
