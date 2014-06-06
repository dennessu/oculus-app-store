package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.identity.spec.v1.option.list.UserTFAListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
interface UserTFAValidator {
    Promise<UserTFA> validateForGet(UserId userId, UserTFAId userTFAId)
    Promise<Void> validateForSearch(UserTFAListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTFA userTFA)
    Promise<Void> validateForUpdate(UserId userId, UserTFAId userTFAId, UserTFA userTFA, UserTFA oldUserTFA)
}
