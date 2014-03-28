package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserLoginAttemptId
import com.junbo.identity.spec.model.users.UserLoginAttempt
import com.junbo.identity.spec.options.list.UserLoginAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
interface UserLoginAttemptValidator {
    Promise<UserLoginAttempt> validateForGet(UserLoginAttemptId userLoginAttemptId)
    Promise<Void> validateForSearch(UserLoginAttemptListOptions options)
    Promise<Void> validateForCreate(UserLoginAttempt userLoginAttempt)
}
