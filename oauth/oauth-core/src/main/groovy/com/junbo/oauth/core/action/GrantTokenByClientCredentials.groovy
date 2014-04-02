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
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.spec.model.AccessToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * GrantTokenByClientCredentials.
 */
@CompileStatic
class GrantTokenByClientCredentials implements Action {

    private TokenService tokenService

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def client = contextWrapper.client

        Assert.notNull(oauthInfo, 'oauthInfo is null')
        Assert.notNull(client, 'client is null')

        AccessToken accessToken = tokenService.generateAccessToken(client, 0L, oauthInfo.scopes)

        contextWrapper.accessToken = accessToken

        return Promise.pure(null)
    }
}
