/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.core.Cookie

/**
 * LoadLoginState.
 */
@CompileStatic
class LoadLoginState implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadLoginState)

    private LoginStateRepository loginStateRepository

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def cookieMap = contextWrapper.cookieMap

        Cookie loginStateCookie = cookieMap.get(OAuthParameters.COOKIE_LOGIN_STATE)

        if (loginStateCookie == null || StringUtils.isEmpty(loginStateCookie.value)) {
            return Promise.pure(null)
        }

        LoginState loginState = loginStateRepository.get(loginStateCookie.value)
        if (loginState == null) {
            LOGGER.warn("The login state $loginStateCookie.value is invalid, silently ignore")
            return Promise.pure(null)
        }

        contextWrapper.loginState = loginState

        return Promise.pure(null)
    }
}
