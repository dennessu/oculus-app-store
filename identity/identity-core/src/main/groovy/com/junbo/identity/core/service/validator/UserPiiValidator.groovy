package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserPiiId
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
interface UserPiiValidator {

    Promise<UserPii> validateForGet(UserPiiId userPiiId)
    Promise<Void> validateForSearch(UserPiiListOptions options)
    Promise<Void> validateForCreate(UserPii userPii)
    Promise<Void> validateForUpdate(UserPiiId userPiiId, UserPii userPii, UserPii oldUserPii)
}
