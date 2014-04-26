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
import com.junbo.oauth.db.repo.RememberMeTokenRepository
import com.junbo.oauth.spec.model.RememberMeToken
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * GrantRememberMeToken.
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
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def needRememberMe = contextWrapper.needRememberMe

        if (!needRememberMe) {
            return Promise.pure(new ActionResult('success'))
        }

        def loginState = contextWrapper.loginState
        Assert.notNull(loginState, 'loginState is null')

        RememberMeToken newToken = new RememberMeToken(
                userId: loginState.userId,
                expiredBy: new Date(System.currentTimeMillis() + defaultRememberMeTokenExpiration * 1000)
        )

        def rememberMeToken = contextWrapper.rememberMeToken
        if (rememberMeToken != null) {
            newToken.tokenValue = rememberMeToken.tokenValue
            newToken.lastAuthDate = rememberMeToken.lastAuthDate
        } else {
            newToken.lastAuthDate = new Date()
        }

        newToken = rememberMeTokenRepository.save(newToken)

        CookieUtil.setCookie(context, OAuthParameters.COOKIE_REMEMBER_ME, newToken.tokenValue,
                defaultRememberMeTokenExpiration)

        return Promise.pure(new ActionResult('success'))
    }
}
