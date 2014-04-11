package com.junbo.identity.data.repository

import com.junbo.common.id.UserPiiId
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
interface UserPiiRepository {
    Promise<UserPii> create(UserPii userPii)

    Promise<UserPii> update(UserPii userPii)

    Promise<UserPii> get(UserPiiId userPiiId)

    Promise<Void> delete(UserPiiId userPiiId)

    Promise<List<UserPii>> search(UserPiiListOptions options)
}
