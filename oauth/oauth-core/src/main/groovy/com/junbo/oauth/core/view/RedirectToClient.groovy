/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.service.OAuthTokenService
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.spec.model.TokenType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.Response

/**
 * RedirectToClient.
 */
@CompileStatic
class RedirectToClient implements Action {

    private OAuthTokenService tokenService

    @Required
    void setTokenService(OAuthTokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        // Get the basic context from constructing an ActionContextWrapper.
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def uriBuilder = UriComponentsBuilder.fromHttpUrl(oauthInfo.redirectUri)

        def authorizationCode = contextWrapper.authorizationCode
        def accessToken = contextWrapper.accessToken
        def idToken = contextWrapper.idToken
        def loginState = contextWrapper.loginState

        Map<String, String> parameters = new HashMap<>()

        // Add the authorization code.
        if (authorizationCode != null) {
            parameters.put(OAuthParameters.CODE, authorizationCode.code)
        }

        // Add the access token and token type (always bearer).
        if (accessToken != null) {
            parameters.put(OAuthParameters.ACCESS_TOKEN, accessToken.tokenValue)
            parameters.put(OAuthParameters.TOKEN_TYPE, TokenType.BEARER.paramName)
        }

        // Add the id token.
        if (idToken != null) {
            parameters.put(OAuthParameters.ID_TOKEN, idToken.tokenValue)
        }

        // Add the state parameter.
        if (oauthInfo.state != null) {
            parameters.put(OAuthParameters.STATE, oauthInfo.state)
        }

        // Add the session state, the client will use it for tracking the login status within an iframe.
        String sessionState = tokenService.generateSessionStatePerClient(
                loginState.sessionId,
                contextWrapper.client.clientId,
                contextWrapper.oauthInfo.redirectUri)

        parameters.put(OAuthParameters.SESSION_STATE, sessionState)

        // If the flow is an implicit flow, which means the token, id_token response type is presented,
        // the parameters need to be added to the redirect uri's fragment.
        if (OAuthInfoUtil.isImplicitFlow(oauthInfo)) {
            List<GString> fragments = parameters.collect { String key, String value ->
                return "$key=$value"
            }

            uriBuilder.fragment(StringUtils.arrayToDelimitedString(fragments.toArray(), '&'))
            // else if only code response type is presented, the parameters need to be added to the query parameters.
        } else {
            parameters.each { String key, String value ->
                uriBuilder.queryParam(key, value)
            }
        }

        contextWrapper.responseBuilder = Response.status(Response.Status.FOUND)
                .location(uriBuilder.build().toUri())

        return Promise.pure(null)
    }
}
