package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
interface UserTFABackupCodeValidator {
    Promise<UserTFABackupCode> validateForGet(UserId userId, UserTFABackupCodeId userTFABackupCodeId)
    Promise<Void> validateForSearch(UserId userId, UserTFABackupCodeListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserTFABackupCode userTFABackupCode)
    Promise<Void> validateForUpdate(UserId userId, UserTFABackupCodeId userTFABackupCodeId,
                                    UserTFABackupCode userTFABackupCode, UserTFABackupCode oldUserTFABackupCode)
}