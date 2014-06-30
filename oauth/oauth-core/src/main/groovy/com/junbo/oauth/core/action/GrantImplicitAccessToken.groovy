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
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.spec.model.ResponseType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * GrantImplicitAccessToken.
 */
@CompileStatic
class GrantImplicitAccessToken implements Action {

    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def client = contextWrapper.client
        def oauthInfo = contextWrapper.oauthInfo

        if (oauthInfo.responseTypes.contains(ResponseType.TOKEN)) {
            def loginState = contextWrapper.loginState
            Assert.notNull(loginState, 'loginState is null')

            def accessToken = tokenService.generateAccessToken(client, loginState.userId, oauthInfo.scopes)

            contextWrapper.accessToken = accessToken
        }

        return Promise.pure(null)

    }
}
