/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.param.OAuthParameters
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.http.HttpStatus

/**
 * VerifyPIN.
 */
class VerifyPIN implements Action {
    private static final String USER_PIN_NOT_FOUND = '131.145'
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyPIN)
    private UserService userService

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap
        def tfaTypes = contextWrapper.TFATypes
        if (!tfaTypes.contains('PIN')) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldInvalid('tfaType').error())
            return Promise.pure(new ActionResult('error'))
        }

        String pin = parameterMap.getFirst(OAuthParameters.PIN)
        if (StringUtils.isEmpty(pin)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldRequired(OAuthParameters.PIN).error())
            return Promise.pure(new ActionResult('error'))
        }

        def user = contextWrapper.user
        def client = contextWrapper.client
        def headerMap = contextWrapper.headerMap
        String clientId = client.clientId
        String userAgent = headerMap.getFirst('user-agent')
        String remoteAddress = contextWrapper.remoteAddress

        try {
            def email = userService.getUserEmailByUserId(user.getId()).get()
            def credentialAttempt = userService.authenticateUser(email, 'PIN', pin, clientId,
                    remoteAddress, userAgent).get()
            if (credentialAttempt == null || !credentialAttempt.succeeded) {
                contextWrapper.errors.add(AppErrors.INSTANCE.invalidCredential().error())
                return Promise.pure(new ActionResult('error'))
            }

            return Promise.pure(new ActionResult('success'))
        } catch (Exception e) {
            if (e instanceof AppErrorException) {
                AppErrorException appError = (AppErrorException) e
                // Exception happened while calling the identity service.
                switch (appError.error.httpStatusCode) {
                    // For response of NOT_FOUND or UNAUTHORIZED, it suggests that
                    // either the username does not exists, or the password is invalid.
                    case HttpStatus.NOT_FOUND.value():
                    case HttpStatus.BAD_REQUEST.value():
                    case HttpStatus.UNAUTHORIZED.value():
                    case HttpStatus.PRECONDITION_FAILED.value():
                        if (appError.error.error().code == USER_PIN_NOT_FOUND) {
                            contextWrapper.errors.add(appError.error.error())
                        } else {
                            contextWrapper.errors.add(AppErrors.INSTANCE.invalidCredential().error())
                        }
                        break
                    // For response of TOO_MANY_REQUESTS, it suggests that the User reaches maximum login attempt
                    case HttpStatus.TOO_MANY_REQUESTS.value():
                        contextWrapper.errors.add(e.error.error())
                        break
                    // For response of FORBIDDEN, it suggests that captcha is required for user login.
                    case HttpStatus.FORBIDDEN.value():
                        // TODO: wait for identity response of captcha required
                        break
                    // For response of INTERNAL_SERVER_ERROR, it suggests that server error happened within the identity
                    // service, throw internal server error exception to the user.
                    case HttpStatus.INTERNAL_SERVER_ERROR.value():
                    default:
                        LOGGER.error('Error calling the identity service.', e)
                        contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
                        break
                }
            } else {
                LOGGER.error('Error calling the identity service.', e)
                contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
            }
            return Promise.pure(new ActionResult('error'))
        }
    }
}
