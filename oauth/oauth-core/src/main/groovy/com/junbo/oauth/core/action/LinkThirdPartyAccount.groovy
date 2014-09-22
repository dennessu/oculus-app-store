/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.resource.AuthenticatorResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.model.LoginState
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * LinkThirdPartyAccount.
 */
@CompileStatic
class LinkThirdPartyAccount implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkThirdPartyAccount)

    private AuthenticatorResource authenticatorResource

    @Required
    void setAuthenticatorResource(AuthenticatorResource authenticatorResource) {
        this.authenticatorResource = authenticatorResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def user = contextWrapper.user
        def thirdPartyAccount = contextWrapper.thirdPartyAccount

        Assert.notNull(user, 'user is null')
        Assert.notNull(thirdPartyAccount, 'thirdPartyAccount is null')

        def authenticator = new UserAuthenticator(
                userId: user.id as UserId,
                type: thirdPartyAccount.type,
                externalId: thirdPartyAccount.externalId
        )

        return authenticatorResource.create(authenticator).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { UserAuthenticator newAuthenticator ->
            if (newAuthenticator == null) {
                return Promise.pure(new ActionResult('error'))
            }

            LoginState loginState = new LoginState(
                    userId: (user.id as UserId).value,
                    lastAuthDate: new Date()
            )

            contextWrapper.loginState = loginState

            return Promise.pure(new ActionResult('success'))
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
        }
    }
}
