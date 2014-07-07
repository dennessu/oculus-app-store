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
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * SaveLoginState.
 */
@CompileStatic
class SaveLoginState implements Action {

    private LoginStateRepository loginStateRepository

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def loginState = contextWrapper.loginState

        Assert.notNull(loginState, 'loginState is null')
        loginState = loginStateRepository.save(loginState)
        contextWrapper.loginState = loginState

        CookieUtil.setCookie(context, OAuthParameters.COOKIE_LOGIN_STATE, loginState.getId(), -1)
        CookieUtil.setCookie(context, OAuthParameters.COOKIE_SESSION_STATE, loginState.sessionId, -1, false)

        return Promise.pure(new ActionResult('success'))
    }
}
