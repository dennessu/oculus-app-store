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
import com.junbo.oauth.db.repo.LoginStateRepository
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * RevokeUserTokens.
 */
@CompileStatic
class RevokeUserTokens implements Action {
    private OAuthTokenService tokenService
    private LoginStateRepository loginStateRepository

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def user = contextWrapper.user

        if (user != null && user.getId().value != 0L) {
            tokenService.revokeAccessToken(user.getId().value)
            tokenService.revokeRefreshToken(user.getId().value)
            loginStateRepository.removeByUserId(user.getId().value)
        }

        return Promise.pure(new ActionResult('success'))
    }
}
