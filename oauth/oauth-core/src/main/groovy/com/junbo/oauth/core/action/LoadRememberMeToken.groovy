/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.ServiceContextUtil
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
 * Javadoc.
 */
@CompileStatic
class LoadRememberMeToken implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRememberMeToken)

    private RememberMeTokenRepository rememberMeTokenRepository

    @Required
    void setRememberMeTokenRepository(RememberMeTokenRepository rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository
    }

    @Override
    boolean execute(ServiceContext context) {
        def cookieMap = ServiceContextUtil.getCookieMap(context)

        Cookie rememberMeCookie = cookieMap.get(OAuthParameters.REMEMBER_ME)

        if (rememberMeCookie == null) {
            return true
        }

        RememberMeToken rememberMeToken = rememberMeTokenRepository.getAndRemove(rememberMeCookie.value)

        if (rememberMeToken == null || rememberMeToken.isExpired()) {
            LOGGER.warn("The remember me token $rememberMeCookie.value is invalid, silently ignore")
            return true
        }

        LoginState loginState = new LoginState(
                userId: rememberMeToken.userId,
                lastAuthDate: rememberMeToken.lastAuthDate
        )

        ServiceContextUtil.setNeedRememberMe(context, true)
        ServiceContextUtil.setRememberMeToken(context, rememberMeToken)
        ServiceContextUtil.setLoginState(context, loginState)

        return true
    }
}
