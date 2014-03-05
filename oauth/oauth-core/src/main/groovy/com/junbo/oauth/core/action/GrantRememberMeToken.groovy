/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.RememberMeTokenRepository
import com.junbo.oauth.spec.model.RememberMeToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

import javax.ws.rs.core.NewCookie

/**
 * Javadoc.
 */
@CompileStatic
class GrantRememberMeToken implements Action {
    private RememberMeTokenRepository rememberMeTokenRepository

    private int defaultRememberMeTokenExpiration

    @Required
    void setRememberMeTokenRepository(RememberMeTokenRepository rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository
    }

    @Required
    void setDefaultRememberMeTokenExpiration(int defaultRememberMeTokenExpiration) {
        this.defaultRememberMeTokenExpiration = defaultRememberMeTokenExpiration
    }

    @Override
    boolean execute(ServiceContext context) {
        def needRememberMe = ServiceContextUtil.getNeedRememberMe(context)

        if (!needRememberMe) {
            return true
        }

        def loginState = ServiceContextUtil.getLoginState(context)
        Assert.notNull(loginState)

        RememberMeToken newToken = new RememberMeToken(
                userId: loginState.userId,
                expiredBy: new Date(System.currentTimeMillis() + defaultRememberMeTokenExpiration * 1000)
        )

        def rememberMeToken = ServiceContextUtil.getRememberMeToken(context)
        if (rememberMeToken != null) {
            newToken.tokenValue = rememberMeToken.tokenValue
            newToken.lastAuthDate = rememberMeToken.lastAuthDate
        } else {
            newToken.lastAuthDate = new Date()
        }

        rememberMeTokenRepository.save(newToken)

        setCookie(newToken.tokenValue, context)

        return true
    }

    private void setCookie(String value, ServiceContext context) {
        def request = ServiceContextUtil.getRequest(context)
        URI uri = ((ContainerRequest) request).baseUri

        NewCookie cookie = new NewCookie(OAuthParameters.REMEMBER_ME, value, uri.path,
                uri.host, null, defaultRememberMeTokenExpiration, uri.scheme == 'https')

        List<NewCookie> responseCookieList = ServiceContextUtil.getResponseCookieList(context)
        responseCookieList.add(cookie)
    }
}
