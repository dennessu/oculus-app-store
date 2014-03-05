/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.core.Cookie

/**
 * Javadoc.
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
    boolean execute(ServiceContext context) {
        def cookieMap = ServiceContextUtil.getCookieMap(context)

        Cookie loginStateCookie = cookieMap.get(OAuthParameters.LOGIN_STATE)

        if (loginStateCookie == null) {
            return true
        }

        LoginState loginState = loginStateRepository.get(loginStateCookie.value)
        if (loginState == null) {
            LOGGER.warn("The login state $loginStateCookie.value is invalid, silently ignore")
            return true
        }

        ServiceContextUtil.setLoginState(context, loginState)

        return true
    }
}
