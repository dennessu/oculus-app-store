/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action.webflow

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * ValidateScope.
 */
@CompileStatic
class ValidateScope implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def appClient = contextWrapper.appClient

        String scopeStr = parameterMap.getFirst(OAuthParameters.SCOPE)

        Set<String> scopes = []

        if (StringUtils.hasText(scopeStr)) {
            String[] scopeTokens = scopeStr.split(' ')
            String[] invalidScopes = scopeTokens.findAll {
                String scope -> !appClient.allowedScopes.contains(scope)
            }

            if (invalidScopes.length > 0) {
                throw AppExceptions.INSTANCE.invalidScope(StringUtils.arrayToCommaDelimitedString(invalidScopes)).
                        exception()
            }

            scopes.addAll(scopeTokens)
        } else if (appClient.defaultScopes != null) {
            scopes = appClient.defaultScopes
        } else {
            throw AppExceptions.INSTANCE.missingScope().exception()
        }

        def oauthInfo = contextWrapper.oauthInfo
        oauthInfo.setScopes(scopes)

        return Promise.pure(null)
    }
}
