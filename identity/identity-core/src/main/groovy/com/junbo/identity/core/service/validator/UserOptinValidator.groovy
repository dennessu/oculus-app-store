package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserOptinId
import com.junbo.identity.spec.model.users.UserOptin
import com.junbo.identity.spec.options.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserOptinValidator {
    Promise<UserOptin> validateForGet(UserId userId, UserOptinId userOptinId)
    Promise<Void> validateForSearch(UserOptinListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserOptin userOptin)
    Promise<Void> validateForUpdate(UserId userId, UserOptinId userOptinId,
                                    UserOptin userOptin, UserOptin oldUserOptin)
}