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
 * BuildRedirectUri
 */
@CompileStatic
class BuildRedirectUri implements Action {
    private String errorMessage

    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def uriBuilder = contextWrapper.redirectUriBuilder

        if (uriBuilder == null) {
            uriBuilder = UriComponentsBuilder.fromHttpUrl(oauthInfo.redirectUri)
            contextWrapper.redirectUriBuilder = uriBuilder
        }

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

        if (authorizationCode != null) {
            parameters.put(OAuthParameters.CODE, authorizationCode.code)
        }

        if (accessToken != null) {
            parameters.put(OAuthParameters.ACCESS_TOKEN, accessToken.tokenValue)
            parameters.put(OAuthParameters.TOKEN_TYPE, TokenType.BEARER.paramName)
        }

        if (idToken != null) {
            parameters.put(OAuthParameters.ID_TOKEN, idToken.tokenValue)
        }

        String state = parameterMap.getFirst(OAuthParameters.STATE)
        if (state != null) {
            parameters.put(OAuthParameters.STATE, state)
        }

        parameters.put(OAuthParameters.SESSION_ID, loginState.sessionId)

        if (OAuthInfoUtil.isImplicitFlow(oauthInfo)) {
            List<GString> fragments = parameters.collect { String key, String value ->
                return "$key=$value"
            }

            uriBuilder.fragment(StringUtils.arrayToDelimitedString(fragments.toArray(), '&'))
        } else {
            parameters.each { String key, String value ->
                uriBuilder.queryParam(key, value)
            }
        }

        return Promise.pure(null)
    }
}
