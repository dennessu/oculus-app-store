package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserTFAPhoneBackupCodeAttemptService {

    Promise<UserTFABackupCodeAttempt> get(UserTFABackupCodeAttemptId id)

    Promise<UserTFABackupCodeAttempt> create(UserTFABackupCodeAttempt model)

    Promise<UserTFABackupCodeAttempt> update(UserTFABackupCodeAttempt model, UserTFABackupCodeAttempt oldModel)

    Promise<Void> delete(UserTFABackupCodeAttemptId id)

    Promise<List<UserTFABackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset)
}