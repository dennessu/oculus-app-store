package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleAttemptId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions
import com.junbo.identity.spec.v1.option.model.UserTeleAttemptGetOptions
import com.junbo.identity.spec.v1.resource.UserTeleAttemptResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import javax.transaction.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@Transactional
class UserTeleAttemptResourceImpl implements UserTeleAttemptResource {

    @Override
    Promise<UserTeleAttempt> create(UserId userId, UserTeleAttempt userTeleAttempt) {
        return null
    }

    @Override
    Promise<UserTeleAttempt> get(UserId userId, UserTeleAttemptId userTeleAttemptId,
                                 UserTeleAttemptGetOptions getOptions) {
        return null
    }

    @Override
    Promise<Results<UserTeleAttempt>> list(UserId userId, UserTeleAttemptListOptions listOptions) {
        return null
    }
}
