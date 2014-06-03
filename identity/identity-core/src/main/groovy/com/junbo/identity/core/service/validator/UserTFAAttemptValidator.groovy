package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.identity.spec.v1.option.list.UserTFAAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/4/14.
 */
@CompileStatic
public interface UserTFAAttemptValidator {
    Promise<UserTFAAttempt> validateForGet(UserId userId, UserTFAAttemptId attemptId)
    Promise<Void> validateForSearch(UserId userId, UserTFAAttemptListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTFAAttempt attempt)
}