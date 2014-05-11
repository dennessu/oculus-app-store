/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.service.UserService
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * SendAccountVerifyEmail.
 */
@CompileStatic
class SendAccountVerifyEmail implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendAccountVerifyEmail)

    private UserService userService

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def user = contextWrapper.user
        Assert.notNull(user, 'user is null')
        def request = (ContainerRequest) contextWrapper.request

        return userService.verifyEmailByUserId((UserId)(user.id), contextWrapper.viewLocale, request.baseUri).recover { Throwable e ->
            handleException(e, contextWrapper)
            // Return success no matter the email has been sent or not
            return Promise.pure(new ActionResult('success'))
        }.then {
            return Promise.pure(new ActionResult('success'))
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error when sending account verify email', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingEmail().error())
        }
    }
}
