/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils

/**
 * The AuthenticateUser action.
 * This action will authenticate the user with the username and password.
 */
@CompileStatic
class AuthenticateUser implements Action {
    /**
     * The static final SLF4J logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticateUser)

    /**
     * The UserService to handle the user related logic.
     */
    private UserService userService

    /**
     * How to handle the exception, rethrow it or return 200 response of exception
     */
    private boolean rethrowException

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setRethrowException(boolean rethrowException) {
        this.rethrowException = rethrowException
    }

    /**
     * Override the {@link com.junbo.langur.core.webflow.action.Action}.execute method.
     * @param context The ActionContext contains the execution context.
     * @return The ActionResult contains the transition or other kind of result contains in a map.
     */
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        // Get the basic context from constructing an ActionContextWrapper.
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def client = contextWrapper.client
        def headerMap = contextWrapper.headerMap

        // Get and validate the username and password from the query parameter.
        String username = parameterMap.getFirst(OAuthParameters.USERNAME)
        if (!StringUtils.hasText(username)) {
            // try to get login parameter from POST form, login param can be username or user-email
            username = parameterMap.getFirst(OAuthParameters.LOGIN)
        }
        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        if (!StringUtils.hasText(username)) {
            handleAppError(contextWrapper, AppCommonErrors.INSTANCE.fieldRequired('login or username'))
        }

        if (!StringUtils.hasText(password)) {
            handleAppError(contextWrapper, AppCommonErrors.INSTANCE.parameterRequired('password'))
        }

        if (!contextWrapper.errors.isEmpty()) {
            return Promise.pure(new ActionResult('error'))
        }

        String clientId = client.clientId
        String userAgent = headerMap.getFirst('user-agent')
        String remoteAddress = contextWrapper.remoteAddress

        // HACK
        // TODO: wait for identity response of captcha required
        boolean captchaRequired = false

        return userService.authenticateUser(username, password, clientId, remoteAddress, userAgent).recover { Throwable e ->
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
                        handleAppError(contextWrapper, AppErrors.INSTANCE.invalidCredential())
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
                        handleAppError(contextWrapper, AppErrors.INSTANCE.errorCallingIdentity())
                        break
                }
            } else {
                LOGGER.error('Error calling the identity service.', e)
                handleAppError(contextWrapper, AppErrors.INSTANCE.errorCallingIdentity())
            }

            return Promise.pure(null)
        }.then { UserCredentialVerifyAttempt loginAttempt ->
            if (loginAttempt == null || !loginAttempt.succeeded) {
                handleAppError(contextWrapper, AppErrors.INSTANCE.invalidCredential())
                return Promise.pure(new ActionResult('error'))
            }

            // TODO: wait for identity response of captcha required
            if (captchaRequired && !contextWrapper.captchaSucceed) {
                contextWrapper.captchaRequired = true
                return Promise.pure(new ActionResult('captchaRequired'))
            }

            // Create the LoginState and save it in the ActionContext
            def loginState = new LoginState(
                    userId: loginAttempt.userId.value,
                    lastAuthDate: new Date()
            )

            def oldLoginState = contextWrapper.loginState
            if (oldLoginState != null) {
                loginState.id = oldLoginState.id
                loginState.rev = oldLoginState.rev

                if (loginState.userId == oldLoginState.userId) {
                    loginState.sessionId = oldLoginState.sessionId
                }
            }

            contextWrapper.loginState = loginState

            // Check if the remember me token is needed.
            String rememberMe = parameterMap.getFirst(OAuthParameters.REMEMBER_ME)
            if ('TRUE'.equalsIgnoreCase(rememberMe)) {
                contextWrapper.needRememberMe = true
            }

            return Promise.pure(new ActionResult('success'))
        }
    }

    private void handleAppError(ActionContextWrapper contextWrapper, AppError appError) {
        if (rethrowException) {
            throw appError.exception()
        }

        contextWrapper.errors.add(appError.error())
    }
}
