package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserPinValidator {
    Promise<UserPin> validateForGet(UserId userId, UserPinId userPinId)
    Promise<Void> validateForSearch(UserPinListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserPin userPin)

    Promise<Void> validateForOldPassword(UserId userId, String oldPassword)
}
