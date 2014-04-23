package com.junbo.identity.data.repository

import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleBackupCodeRepository
        extends IdentityBaseRepository<UserTeleBackupCode, UserTeleBackupCodeId>  {
    @ReadMethod
    Promise<List<UserTeleBackupCode>> search(UserTeleBackupCodeListOptions listOptions)
}