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
import com.junbo.oauth.core.util.UriUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic

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
            throw AppCommonErrors.INSTANCE.fieldInvalid('redirect_uri', redirectUri).exception()
        }

        boolean allowed = client.redirectUris.any {
            String allowedRedirectUri -> UriUtil.match(redirectUri, allowedRedirectUri)
        }

        if (!allowed) {
            throw AppCommonErrors.INSTANCE.parameterRequired('redirect_uri').exception()
        }

        def oauthInfo = contextWrapper.oauthInfo
        oauthInfo.redirectUri = redirectUri

        return Promise.pure(null)
    }
}
