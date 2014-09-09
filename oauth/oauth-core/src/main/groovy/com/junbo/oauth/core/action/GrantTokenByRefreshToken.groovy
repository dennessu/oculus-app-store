/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.spec.model.AccessToken
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.model.RefreshToken
import com.junbo.oauth.spec.model.Scope
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * GrantTokenByRefreshToken.
 */
@CompileStatic
class GrantTokenByRefreshToken implements Action {

    private OAuthTokenService tokenService

    private ScopeRepository scopeRepository

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Required
    void setScopeRepository(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def client = contextWrapper.client
        def oauthInfo = contextWrapper.oauthInfo

        String token = parameterMap.getFirst(OAuthParameters.REFRESH_TOKEN)

        if (!StringUtils.hasText(token)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('refresh_token').exception()
        }

        RefreshToken refreshToken = tokenService.getAndRemoveRefreshToken(token)
        if (refreshToken == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('refresh_token', token).exception()
        }

        if (refreshToken.isExpired()) {
            throw AppErrors.INSTANCE.expiredRefreshToken(token).exception()
        }

        AccessToken accessToken = refreshToken.accessToken
        Assert.notNull(accessToken)

        if (refreshToken.clientId != client.clientId) {
            throw AppErrors.INSTANCE.differentClientId(refreshToken.clientId, client.clientId).exception()
        }

        Set<String> scopesParam = oauthInfo.scopes

        String[] extraScopes = scopesParam.findAll {
            String scope -> !accessToken.scopes.contains(scope)
        }

        if (extraScopes.length > 0) {
            throw AppErrors.INSTANCE.outboundScope().exception()
        }

        oauthInfo.scopes.each { String scopeString ->
            Scope scope = scopeRepository.getScope(scopeString)
            if (scope != null && scope.tfaRequired) {
                throw AppCommonErrors.INSTANCE.forbiddenWithMessage("Scope $scopeString is not allowed to " +
                        "be granted by refresh token").exception()
            }
        }

        LoginState loginState = new LoginState(
                userId: refreshToken.userId,
                lastAuthDate: new Date()
        )
        contextWrapper.loginState = loginState

        String ipRestriction = parameterMap.getFirst(OAuthParameters.IP_RESTRICTION)
        Boolean ipRestrictionRequired = Boolean.parseBoolean(ipRestriction);

        def newAccessToken = tokenService.generateAccessToken(client, refreshToken.userId,
                scopesParam, ipRestrictionRequired, contextWrapper.overrideExpiration)
        contextWrapper.accessToken = newAccessToken

        def newRefreshToken = tokenService.generateRefreshToken(client, newAccessToken, refreshToken)
        contextWrapper.refreshToken = newRefreshToken

        return Promise.pure(null)
    }
}
