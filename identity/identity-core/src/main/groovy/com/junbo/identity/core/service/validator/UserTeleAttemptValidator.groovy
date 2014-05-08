package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleAttemptId
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/4/14.
 */
@CompileStatic
public interface UserTeleAttemptValidator {
    Promise<UserTeleAttempt> validateForGet(UserId userId, UserTeleAttemptId attemptId)
    Promise<Void> validateForSearch(UserId userId, UserTeleAttemptListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTeleAttempt attempt)
}