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
import com.junbo.oauth.core.util.CookieUtil
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.db.repo.RememberMeTokenRepository
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.core.Response

/**
 * ClearLoginCookies.
 */
@CompileStatic
class ClearLoginCookies implements Action {
    private LoginStateRepository loginStateRepository
    private RememberMeTokenRepository rememberMeTokenRepository

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Required
    void setRememberMeTokenRepository(RememberMeTokenRepository rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def loginState = contextWrapper.loginState

        if (loginState != null) {
            loginStateRepository.delete(loginState.id)
        }

        CookieUtil.clearCookie(OAuthParameters.LOGIN_STATE, context)

        def rememberMeToken = contextWrapper.rememberMeToken

        if (rememberMeToken != null) {
            rememberMeTokenRepository.getAndRemove(rememberMeToken.tokenValue)
        }

        CookieUtil.clearCookie(OAuthParameters.REMEMBER_ME, context)

        if (contextWrapper.responseBuilder == null) {
            contextWrapper.responseBuilder = Response.ok()
        }

        return Promise.pure(null)
    }
}
