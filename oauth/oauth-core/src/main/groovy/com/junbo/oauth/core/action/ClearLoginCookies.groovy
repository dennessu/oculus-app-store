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
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.Response

/**
 * The ClearLoginCookies action.
 * This action will clear the login related cookies, including the login state and the remember me token cookie.
 * @author Zhanxin Yang.
 */
@CompileStatic
class ClearLoginCookies implements Action {
    /**
     * The LoginStateRepository to handle the login state related logic.
     */
    private LoginStateRepository loginStateRepository

    /**
     * The RememberMeTokenRepository to handle the remember me token related logic.
     */
    private RememberMeTokenRepository rememberMeTokenRepository

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Required
    void setRememberMeTokenRepository(RememberMeTokenRepository rememberMeTokenRepository) {
        this.rememberMeTokenRepository = rememberMeTokenRepository
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

        def loginState = contextWrapper.loginState

        // Delete the login state from the database if presented.
        if (loginState != null) {
            loginStateRepository.delete(loginState.getId())
        }

        // Clear the login state cookie.
        CookieUtil.clearCookie(context, OAuthParameters.COOKIE_LOGIN_STATE)

        // Clear the session state cookie.
        CookieUtil.clearCookie(context, OAuthParameters.COOKIE_SESSION_STATE)

        def rememberMeToken = contextWrapper.rememberMeToken

        // Delete the remember me token from the database if presented.
        if (rememberMeToken != null) {
            rememberMeTokenRepository.getAndRemove(rememberMeToken.tokenValue)
        }

        // Clear the remember me token cookie.
        CookieUtil.clearCookie(context, OAuthParameters.COOKIE_REMEMBER_ME)

        // Return an OK(200) response.
        if (contextWrapper.responseBuilder == null) {
            contextWrapper.responseBuilder = Response.ok()
        }

        if (contextWrapper.redirectUri != null) {
            def uriBuilder = UriComponentsBuilder.fromHttpUrl(contextWrapper.redirectUri)
            uriBuilder.queryParam('logout', 'y')
            contextWrapper.redirectUri = uriBuilder.build().toUriString()
        }

        return Promise.pure(null)
    }
}
