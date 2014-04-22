package com.junbo.identity.data.repository

import com.junbo.common.id.UserPiiId
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
interface UserPiiRepository extends IdentityBaseRepository<UserPii, UserPiiId> {
    @ReadMethod
    Promise<List<UserPii>> search(UserPiiListOptions options)
}
