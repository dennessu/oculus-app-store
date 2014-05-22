/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.resource.UserCredentialResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * CreateUserCredential.
 */
@CompileStatic
class CreateUserCredential implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserCredential)
    private UserCredentialResource userCredentialResource

    @Required
    void setUserCredentialResource(UserCredentialResource userCredentialResource) {
        this.userCredentialResource = userCredentialResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap
        def user = contextWrapper.user

        Assert.notNull(user, 'user is null')

        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        String pin = parameterMap.getFirst(OAuthParameters.PIN)

        UserCredential passwordCredential = new UserCredential(
                value: password,
                type: 'PASSWORD'
        )

        UserCredential pinCredential = new UserCredential(
                value: pin,
                type: 'PIN'
        )

        return userCredentialResource.create((UserId) user.id, passwordCredential).recover { Throwable throwable ->
            handleException(throwable, contextWrapper)

            return Promise.pure(null)
        }.then { UserCredential newUserCredential ->
            if (newUserCredential == null) {
                return Promise.pure(new ActionResult('error'))
            }

            return Promise.pure(new ActionResult('success'))
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            return userCredentialResource.create(user.id as UserId, pinCredential).recover { Throwable throwable ->
                handleException(throwable, contextWrapper)

                return Promise.pure(null)
            }.then { UserCredential newUserCredential ->
                if (newUserCredential == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                return Promise.pure(new ActionResult('success'))
            }
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
        }
    }
}
