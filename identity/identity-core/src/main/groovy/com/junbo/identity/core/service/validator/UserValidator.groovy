package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.langur.core.promise.Promise

/**
 * Created by kg on 3/17/14.
 */
interface UserValidator {
    Promise<User> validateForGet(UserId userId)
    Promise<Void> validateForCreate(User user)
    Promise<Void> validateForUpdate(User user, User oldUser)
    Promise<Void> validateForSearch(UserListOptions options)
    Promise<Void> validateEmail(String email)
    Promise<Void> validateUsername(String username)
    // Check whether username and email is occupied, if yes, return true, if no, return false
    Promise<Boolean> validateUsernameEmailBlocker(String username, String email)
}
