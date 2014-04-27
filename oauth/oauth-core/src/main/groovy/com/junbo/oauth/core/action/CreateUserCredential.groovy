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
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * CreateUserCredential.
 */
@CompileStatic
class CreateUserCredential implements Action {
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

        if (StringUtils.isEmpty(password)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingPassword().error())
            return Promise.pure(new ActionResult('error'))
        }

        UserCredential userCredential = new UserCredential(
                value: new String(Base64.encodeBase64(password.bytes)),
                type: 'PASSWORD'
        )

        userCredentialResource.create((UserId) user.id, userCredential).recover { Throwable throwable ->
            if (throwable instanceof AppErrorException) {
                contextWrapper.errors.add(((AppErrorException) throwable).error.error())
            } else {
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
            }

            return Promise.pure(null)
        }.then { UserCredential newUserCredential ->
            if (newUserCredential == null) {
                return Promise.pure(new ActionResult('error'))
            }

            contextWrapper.userCredential = newUserCredential
            return Promise.pure(new ActionResult('success'))
        }
    }
}
