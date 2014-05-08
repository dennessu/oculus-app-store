package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
interface UserTeleBackupCodeValidator {
    Promise<UserTeleBackupCode> validateForGet(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId)
    Promise<Void> validateForSearch(UserId userId, UserTeleBackupCodeListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTeleBackupCode userTeleBackupCode)
    Promise<Void> validateForUpdate(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId,
                                    UserTeleBackupCode userTeleBackupCode, UserTeleBackupCode oldUserTeleBackupCode)
}