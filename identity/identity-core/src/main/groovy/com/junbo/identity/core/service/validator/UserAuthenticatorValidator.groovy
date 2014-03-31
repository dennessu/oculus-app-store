package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.model.users.UserAuthenticator
import com.junbo.identity.spec.options.list.UserAuthenticatorListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Created by liangfu on 3/27/14.
 */
@Component
@CompileStatic
interface UserAuthenticatorValidator {
    Promise<UserAuthenticator> validateForGet(UserId userId, UserAuthenticatorId userAuthenticatorId)
    Promise<Void> validateForSearch(UserAuthenticatorListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserAuthenticator userAuthenticator)
    Promise<Void> validateForUpdate(UserId userId, UserAuthenticatorId userAuthenticatorId,
                                    UserAuthenticator authenticator, UserAuthenticator oldAuthenticator)
}