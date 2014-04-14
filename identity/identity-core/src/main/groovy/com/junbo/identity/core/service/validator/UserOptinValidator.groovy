package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserOptinId
import com.junbo.identity.spec.v1.model.UserOptin
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserOptinValidator {
    Promise<UserOptin> validateForGet(UserOptinId userOptinId)
    Promise<Void> validateForSearch(UserOptinListOptions options)
    Promise<Void> validateForCreate(UserOptin userOptin)
    Promise<Void> validateForUpdate(UserOptinId userOptinId, UserOptin userOptin, UserOptin oldUserOptin)
}