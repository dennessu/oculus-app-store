package com.junbo.identity.data.repository

import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleBackupCodeRepository
        extends BaseRepository<UserTeleBackupCode, UserTeleBackupCodeId> {
    @ReadMethod
    Promise<List<UserTeleBackupCode>> search(UserTeleBackupCodeListOptions listOptions)
}