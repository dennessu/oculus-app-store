package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserEmailId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.model.users.UserEmail
import com.junbo.identity.spec.options.list.UserEmailListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserEmailValidator {
    Promise<UserEmail> validateForGet(UserId userId, UserEmailId userEmailId)
    Promise<Void> validateForSearch(UserEmailListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserEmail userEmail)
    Promise<Void> validateForUpdate(UserId userId, UserEmailId userEmailId, UserEmail userEmail, UserEmail oldUserEmail)
}
