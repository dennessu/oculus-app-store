package com.junbo.identity.core.service.validator

import com.junbo.identity.spec.model.users.User
import com.junbo.langur.core.promise.Promise

/**
 * Created by kg on 3/17/14.
 */
interface UserValidator {

    Promise<Void> validateForCreate(User user)

    Promise<Void> validateForUpdate(User user, User oldUser)
}
