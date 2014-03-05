/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.LoginStateRepository
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
class SaveLoginState implements Action {
    private LoginStateRepository loginStateRepository

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Override
    boolean execute(ServiceContext context) {
        def loginState = ServiceContextUtil.getLoginState(context)

        Assert.notNull(loginState)
        loginStateRepository.saveOrUpdate(loginState)

        setCookie(loginState.id, context)

        return true
    }

    private static void setCookie(String value, ServiceContext context) {
        def request = ServiceContextUtil.getRequest(context)
        URI uri = ((ContainerRequest) request).baseUri

        NewCookie cookie = new NewCookie(OAuthParameters.LOGIN_STATE, value, uri.path,
                uri.host, null, -1, uri.scheme == 'https')

        List<NewCookie> responseCookieList = ServiceContextUtil.getResponseCookieList(context)
        responseCookieList.add(cookie)
    }
}
