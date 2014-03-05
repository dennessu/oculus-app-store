/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.TokenType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder

/**
 * Javadoc.
 */
@CompileStatic
class BuildRedirectUri implements Action {
    @Override
    boolean execute(ServiceContext context) {
        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        def authorizationCode = ServiceContextUtil.getAuthorizationCode(context)
        def accessToken = ServiceContextUtil.getAccessToken(context)
        def idToken = ServiceContextUtil.getIdToken(context)

        UriComponentsBuilder uriBuilder = ServiceContextUtil.getRedirectUriBuilder(context)

        if (uriBuilder == null) {
            uriBuilder = UriComponentsBuilder.fromHttpUrl(oauthInfo.redirectUri)
            ServiceContextUtil.setRedirectUriBuilder(context, uriBuilder)
        }

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

        if (OAuthInfoUtil.isImplicitFlow(oauthInfo)) {
            List<String> fragments = parameters.collect { String key, String value ->
                return key + '=' + value
            }

            uriBuilder.fragment(StringUtils.arrayToDelimitedString(fragments.toArray(), '&'))

        } else {
            parameters.each { String key, String value ->
                uriBuilder.queryParam(key, value)
            }
        }

        return true
    }
}
