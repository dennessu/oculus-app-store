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
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.db.repo.RememberMeTokenRepository
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.model.RememberMeToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.core.Cookie

/**
 * LoadRememberMeToken.
 */
@CompileStatic
class LoadRememberMeToken implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRememberMeToken)

    private RememberMeTokenRepository rememberMeTokenRepository

    private UserService userService

    @Required
    void setRememberMeTokenRepository(RememberMeTokenRepository rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def cookieMap = contextWrapper.cookieMap

        Cookie rememberMeCookie = cookieMap.get(OAuthParameters.COOKIE_REMEMBER_ME)

        if (rememberMeCookie == null) {
            return Promise.pure(null)
        }

        RememberMeToken rememberMeToken = rememberMeTokenRepository.getAndRemove(rememberMeCookie.value)

        if (rememberMeToken == null || rememberMeToken.isExpired()) {
            LOGGER.warn("The remember me token $rememberMeCookie.value is invalid, silently ignore")
            return Promise.pure(null)
        }

        LoginState loginState = new LoginState(
                userId: rememberMeToken.userId,
                lastAuthDate: rememberMeToken.lastAuthDate
        )

        contextWrapper.needRememberMe = true
        contextWrapper.rememberMeToken = rememberMeToken
        contextWrapper.loginState = loginState

        try {
            contextWrapper.user = userService.getUser(new UserId(loginState.userId)).get()
        } catch (AppErrorException e) {
            if (e.error.httpStatusCode == 404) {
                LOGGER.warn("The remember me token $rememberMeCookie.value is invalid, silently ignore")
                contextWrapper.loginState = null
                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }
}
