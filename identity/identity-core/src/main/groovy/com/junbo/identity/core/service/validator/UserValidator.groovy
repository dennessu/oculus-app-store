package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise

/**
 * Created by kg on 3/17/14.
 */
interface UserValidator {
    Promise<User> validateForGet(UserId userId)
    Promise<Void> validateForCreate(User user)
    Promise<Void> validateForUpdate(User user, User oldUser)
    Promise<Void> validateForSearch(UserGetOptions options)
}
