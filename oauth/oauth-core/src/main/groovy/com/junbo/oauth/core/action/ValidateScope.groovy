/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.OAuthInfo
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class ValidateScope implements Action {

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)
        def appClient = ServiceContextUtil.getAppClient(context)

        String scopeStr = parameterMap.getFirst(OAuthParameters.SCOPE)

        Set<String> scopes = []

        if (StringUtils.hasText(scopeStr)) {
            String[] scopeTokens = scopeStr.split(' ')
            String[] invalidScopes = scopeTokens.findAll {
                String scope -> !appClient.allowedScopes.contains(scope)
            }

            if (invalidScopes.length > 0) {
                throw AppExceptions.INSTANCE.invalidScope(StringUtils.arrayToCommaDelimitedString(invalidScopes))
                        .exception()
            }

            scopes.addAll(scopeTokens)
        } else if (appClient.defaultScopes != null) {
            scopes = appClient.defaultScopes
        } else {
            throw AppExceptions.INSTANCE.missingScope().exception()
        }

        OAuthInfo oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        oauthInfo.setScopes(scopes)

        return true
    }
}
