/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
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

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
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
        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        if (!StringUtils.hasText(username)) {
            throw AppExceptions.INSTANCE.missingUsername().exception()
        }

        if (!StringUtils.hasText(password)) {
            throw AppExceptions.INSTANCE.missingPassword().exception()
        }

        // Authenticate the user will the username and password.
        try {
            userService.authenticateUser(username, password, client.clientId, '1.1.1.1',
                    headerMap.getFirst('user-agent'))
                    .then { UserCredentialVerifyAttempt loginAttempt ->
                if (loginAttempt == null || !loginAttempt.succeeded) {
                    throw AppExceptions.INSTANCE.invalidCredential().exception()
                }

                // Create the LoginState and save it in the ActionContext
                def loginState = new LoginState(
                        userId: loginAttempt.userId.value,
                        lastAuthDate: new Date()
                )

                contextWrapper.loginState = loginState

                // Check if the remember me token is needed.
                String rememberMe = parameterMap.getFirst(OAuthParameters.REMEMBER_ME)
                if ('TRUE'.equalsIgnoreCase(rememberMe)) {
                    contextWrapper.needRememberMe = true
                }

                return Promise.pure(new ActionResult('success'))
            }
        } catch (AppErrorException e) {
            // Exception happened while calling the identity service.
            switch (e.error.httpStatusCode) {
            // For response of NOT_FOUND or UNAUTHORIZED, then it suggests that either the username does not exists,
            // or the password is invalid.
                case HttpStatus.NOT_FOUND:
                case HttpStatus.UNAUTHORIZED:
                    throw AppExceptions.INSTANCE.invalidCredential().exception()
            // For response of INTERNAL_SERVER_ERROR, it suggests that server error happened within the identity
            // service, throw internal server error exception to the user.
                case HttpStatus.INTERNAL_SERVER_ERROR:
                default:
                    LOGGER.error('Error calling the identity service.', e)
                    throw AppExceptions.INSTANCE.errorCallingIdentity().exception()
            }
        }
    }
}
