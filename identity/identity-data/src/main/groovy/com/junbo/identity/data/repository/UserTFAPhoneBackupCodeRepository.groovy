package com.junbo.identity.data.repository

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTFAPhoneBackupCodeRepository extends BaseRepository<UserTFABackupCode, UserTFABackupCodeId> {
    @ReadMethod
    Promise<List<UserTFABackupCode>> searchByUserId(UserId userId, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<UserTFABackupCode>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit,
                                                                    Integer offset)
}