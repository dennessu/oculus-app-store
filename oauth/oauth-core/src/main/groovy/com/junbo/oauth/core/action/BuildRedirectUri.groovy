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
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.spec.model.TokenType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder

/**
 * The BuildRedirectUri action.
 * This action will build the redirect uri for the authorize flow, attaching the authorization code, access token
 * or id token to the client's redirect uri's query parameter or fragment.
 * It is also used for sending error message to the redirect uri.
 * @author Zhanxin Yang
 */
@CompileStatic
class BuildRedirectUri implements Action {
    /**
     * The error Message to be send. If not present, then this action is used for sending the authorize result.
     */
    private String errorMessage

    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
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

        def oauthInfo = contextWrapper.oauthInfo
        def uriBuilder = contextWrapper.redirectUriBuilder

        if (uriBuilder == null) {
            uriBuilder = UriComponentsBuilder.fromHttpUrl(oauthInfo.redirectUri)
            contextWrapper.redirectUriBuilder = uriBuilder
        }

        // If the errorMessage is present, add the error message as a query parameter and return.
        if (errorMessage != null) {
            uriBuilder.queryParam(OAuthParameters.ERROR, errorMessage)
            return Promise.pure(null)
        }

        def parameterMap = contextWrapper.parameterMap
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
        String state = parameterMap.getFirst(OAuthParameters.STATE)
        if (state != null) {
            parameters.put(OAuthParameters.STATE, state)
        }

        // Add the session state, the client will use it for tracking the login status within an iframe.
        parameters.put(OAuthParameters.SESSION_ID, loginState.sessionId)

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

        return Promise.pure(null)
    }
}
