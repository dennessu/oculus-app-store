package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import com.junbo.identity.spec.v1.option.model.UserTeleBackupCodeAttemptGetOptions
import com.junbo.identity.spec.v1.resource.UserTeleBackupCodeAttemptResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import javax.transaction.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTeleBackupCodeAttemptResourceImpl implements UserTeleBackupCodeAttemptResource {

    @Override
    Promise<UserTeleBackupCodeAttempt> create(UserId userId, UserTeleBackupCodeAttempt userTeleBackupCodeAttempt) {
        return null
    }

    @Override
    Promise<UserTeleBackupCodeAttempt> get(UserId userId, UserTeleBackupCodeAttemptId userTeleBackupCodeAttemptId,
                UserTeleBackupCodeAttemptGetOptions getOptions) {
        return null
    }

    @Override
    Promise<Results<UserTeleBackupCodeAttempt>> list(UserId userId, UserTeleBackupCodeAttemptListOptions listOptions) {
        return null
    }
}
