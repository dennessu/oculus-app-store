package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
interface UserTFABackupCodeAttemptValidator {
    Promise<UserTFABackupCodeAttempt> validateForGet(UserId userId, UserTFABackupCodeAttemptId attemptId)
    Promise<Void> validateForSearch(UserId userId, UserTFABackupCodeAttemptListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTFABackupCodeAttempt attempt)
}