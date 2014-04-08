package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Created by liangfu on 3/27/14.
 */
@Component
@CompileStatic
interface UserAuthenticatorValidator {
    Promise<UserAuthenticator> validateForGet(UserAuthenticatorId userAuthenticatorId)
    Promise<Void> validateForSearch(AuthenticatorListOptions options)
    Promise<Void> validateForCreate(UserAuthenticator userAuthenticator)
    Promise<Void> validateForUpdate(UserAuthenticatorId userAuthenticatorId,
                                    UserAuthenticator authenticator, UserAuthenticator oldAuthenticator)
}