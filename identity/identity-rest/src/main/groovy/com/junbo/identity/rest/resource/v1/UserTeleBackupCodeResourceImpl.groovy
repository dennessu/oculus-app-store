package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
import com.junbo.identity.spec.v1.option.model.UserTeleBackupCodeGetOptions
import com.junbo.identity.spec.v1.resource.UserTeleBackupCodeResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import javax.transaction.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTeleBackupCodeResourceImpl implements UserTeleBackupCodeResource {

    @Override
    Promise<UserTeleBackupCode> create(UserId userId, UserTeleBackupCode userTeleBackupCode) {
        return null
    }

    @Override
    Promise<UserTeleBackupCode> get(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId,
            UserTeleBackupCodeGetOptions getOptions) {
        return null
    }

    @Override
    Promise<Void> delete(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId) {
        return null
    }

    @Override
    Promise<Results<UserTeleBackupCode>> list(UserId userId, UserTeleBackupCodeListOptions listOptions) {
        return null
    }
}
