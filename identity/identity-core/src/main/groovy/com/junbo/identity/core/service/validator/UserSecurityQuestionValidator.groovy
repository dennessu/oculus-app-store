package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.identity.spec.model.users.UserSecurityQuestion
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
interface UserSecurityQuestionValidator {
    Promise<UserSecurityQuestion> validateForGet(UserId userId, UserSecurityQuestionId userSecurityQuestionId)
    Promise<Void> validateForSearch(UserSecurityQuestionListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserSecurityQuestion userSecurityQuestion)
}