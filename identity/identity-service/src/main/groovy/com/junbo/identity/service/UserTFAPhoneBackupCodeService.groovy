package com.junbo.identity.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
public interface UserTFAPhoneBackupCodeService {
    Promise<UserTFABackupCode> get(UserTFABackupCodeId id)

    Promise<UserTFABackupCode> create(UserTFABackupCode model)

    Promise<UserTFABackupCode> update(UserTFABackupCode model, UserTFABackupCode oldModel)

    Promise<Void> delete(UserTFABackupCodeId id)

    Promise<Results<UserTFABackupCode>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<Results<UserTFABackupCode>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit,
                                                                   Integer offset)
}