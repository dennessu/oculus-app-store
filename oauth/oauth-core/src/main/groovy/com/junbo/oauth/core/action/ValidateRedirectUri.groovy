/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * Javadoc.
 */
@CompileStatic
class ValidateRedirectUri implements Action {

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        def appClient = ServiceContextUtil.getAppClient(context)

        String redirectUri = parameterMap.getFirst(OAuthParameters.REDIRECT_URI)

        if (redirectUri == null) {
            redirectUri = appClient.defaultRedirectUri
        }

        if (redirectUri == null) {
            throw AppExceptions.INSTANCE.invalidRedirectUri(redirectUri).exception()
        }

        boolean allowed = appClient.allowedRedirectUris.any {
            String allowedRedirectUri -> match(redirectUri, allowedRedirectUri)
        }

        if (!allowed) {
            throw AppExceptions.INSTANCE.missingRedirectUri().exception()
        }

        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        oauthInfo.redirectUri = redirectUri

        return true
    }

    private static boolean match(String redirectUri, String allowedRedirectUri) {
        if (allowedRedirectUri.contains('*')) {
            String redirectUriPattern = allowedRedirectUri.replace('.', '\\.').replace('?', '\\?')
                    .replace('*', '.*').concat('.*')
            if (Pattern.matches(redirectUriPattern, redirectUri)) {
                return true
            }
        } else if (redirectUri.startsWith(allowedRedirectUri)) {
            return true
        }

        return false
    }
}
