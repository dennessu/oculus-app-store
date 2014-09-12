package com.junbo.identity.rest.resource.v1

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserLogId
import com.junbo.common.model.Results
import com.junbo.common.userlog.UserLog
import com.junbo.common.userlog.UserLogListOptions
import com.junbo.common.userlog.UserLogRepository
import com.junbo.identity.spec.v1.resource.UserLogResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.transaction.Transactional

/**
 * UserLog Resource impl.
 */
@CompileStatic
@Transactional
class UserLogResourceImpl implements UserLogResource {
    @Autowired
    UserLogRepository userLogRepo

    @Override
    Promise<UserLog> get(UserLogId userLogId) {
        return userLogRepo.get(userLogId).then { UserLog userLog ->
            return Promise.pure(userLog)
        }
    }

    @Override
    Promise<Results<UserLog>> list(UserLogListOptions listOptions) {
        checkListOptions(listOptions)
        Results<UserLog> result = userLogRepo.list(listOptions)
        return Promise.pure(result)
    }

    private void checkListOptions(UserLogListOptions options) {
        if (options.userId == null && options.clientId == null && options.apiName == null &&
                options.httpMethod == null && options.sequenceId == null && options.isOK == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid(null,
                    "one of userId, clientId, apiName, httpMethod, sequenceId and isOK should be provided").exception()
        }
    }
}
