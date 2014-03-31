package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.options.list.UserPasswordListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserPasswordValidator {
    Promise<UserPassword> validateForGet(UserId userId, UserPasswordId userPasswordId)
    Promise<Void> validateForSearch(UserPasswordListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserPassword userPassword)
}