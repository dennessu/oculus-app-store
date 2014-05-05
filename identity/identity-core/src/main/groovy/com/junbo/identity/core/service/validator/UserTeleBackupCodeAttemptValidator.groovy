package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
interface UserTeleBackupCodeAttemptValidator {
    Promise<UserTeleBackupCodeAttempt> validateForGet(UserId userId, UserTeleBackupCodeAttemptId attemptId)
    Promise<Void> validateForSearch(UserId userId, UserTeleBackupCodeAttemptListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTeleBackupCodeAttempt attempt)
}