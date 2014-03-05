/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action.webflow

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.service.TokenGenerationService
import com.junbo.oauth.spec.model.AccessToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * GrantTokenByPassword.
 */
@CompileStatic
class GrantTokenByPassword implements Action {

    private TokenGenerationService tokenGenerationService

    @Required
    void setTokenGenerationService(TokenGenerationService tokenGenerationService) {
        this.tokenGenerationService = tokenGenerationService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def appClient = contextWrapper.appClient
        def loginState = contextWrapper.loginState

        Assert.notNull(oauthInfo, 'oauthInfo is null')
        Assert.notNull(appClient, 'appClient is null')
        Assert.notNull(loginState, 'loginState is null')

        AccessToken accessToken = tokenGenerationService.generateAccessToken(appClient,
                loginState.userId, oauthInfo.scopes)

        contextWrapper.accessToken = accessToken

        return Promise.pure(null)
    }
}
