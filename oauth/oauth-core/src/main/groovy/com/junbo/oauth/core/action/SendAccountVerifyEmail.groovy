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
import com.junbo.oauth.clientproxy.facebook.sentry.SentryFacade
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.service.UserService
import com.junbo.store.spec.model.external.sentry.SentryCategory
import com.junbo.store.spec.model.external.sentry.SentryResponse
import groovy.transform.CompileStatic
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

    private SentryFacade sentryFacade

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setSentryFacade(SentryFacade sentryFacade) {
        this.sentryFacade = sentryFacade
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def user = contextWrapper.user
        def loginState = contextWrapper.loginState

        Assert.notNull(user, 'user is null')
        Assert.notNull(loginState, 'loginState is null')
        user.id = new UserId(loginState.userId)

        def textMap = [:]
        return sentryFacade.doSentryCheck(sentryFacade.createSentryRequest(SentryCategory.OCULUS_EMAIL_LOOKUP.value,
                textMap)
        ).recover { Throwable throwable ->
            LOGGER.error('ResendVerifyEmail:  Call sentry error, Ignore', throwable)
            return Promise.pure()
        }.then { SentryResponse sentryResponse ->
            if (sentryResponse != null && sentryResponse.isBlockAccess()) {
                throw AppErrors.INSTANCE.sentryBlockEmailCheck('resend verify email').exception()
            }
            return Promise.pure()
        }.then {
            return userService.sendVerifyEmail((UserId)(user.id), contextWrapper).recover { Throwable e ->
                handleException(e, contextWrapper)
                // Return success no matter the email has been sent or not
                return Promise.pure(new ActionResult('success'))
            }.then {
                return Promise.pure(new ActionResult('success'))
            }
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error when sending account verify email', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingEmail().error())
        }
    }
}
