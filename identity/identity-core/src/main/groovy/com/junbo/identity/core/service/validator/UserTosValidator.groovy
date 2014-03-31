package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosId
import com.junbo.identity.spec.model.users.UserTos
import com.junbo.identity.spec.options.list.UserTosListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
interface UserTosValidator {
    Promise<UserTos> validateForGet(UserId userId, UserTosId userTosId)
    Promise<Void> validateForSearch(UserTosListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTos userTos)
    Promise<Void> validateForUpdate(UserId userId, UserTosId userTosId,
                                    UserTos userTos, UserTos oldUserTos)
}
