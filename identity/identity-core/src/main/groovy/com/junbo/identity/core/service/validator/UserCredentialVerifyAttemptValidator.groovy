package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
interface UserCredentialVerifyAttemptValidator {
    Promise<UserCredentialVerifyAttempt> validateForGet(UserCredentialVerifyAttemptId userLoginAttemptId)
    Promise<Void> validateForSearch(UserCredentialAttemptListOptions options)
    Promise<Void> validateForCreate(UserCredentialVerifyAttempt userLoginAttempt)
}
