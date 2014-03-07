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
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * ValidateRedirectUri.
 */
@CompileStatic
class ValidateRedirectUri implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def client = contextWrapper.client

        String redirectUri = parameterMap.getFirst(OAuthParameters.REDIRECT_URI)

        if (redirectUri == null) {
            redirectUri = client.defaultRedirectUri
        }

        if (redirectUri == null) {
            throw AppExceptions.INSTANCE.invalidRedirectUri(redirectUri).exception()
        }

        boolean allowed = client.allowedRedirectUris.any {
            String allowedRedirectUri -> match(redirectUri, allowedRedirectUri)
        }

        if (!allowed) {
            throw AppExceptions.INSTANCE.missingRedirectUri().exception()
        }

        def oauthInfo = contextWrapper.oauthInfo
        oauthInfo.redirectUri = redirectUri

        return Promise.pure(null)
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
