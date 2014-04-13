package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
interface UserCredentialValidator {
    Promise<Void> validateForSearch(UserId userId, UserCredentialListOptions options)
    Promise<Object> validateForCreate(UserId userId, UserCredential userCredential)
}