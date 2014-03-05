/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.GrantType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class ValidateGrantType implements Action {

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        String grantTypeParam = parameterMap.getFirst(OAuthParameters.GRANT_TYPE)

        if (!StringUtils.hasText(grantTypeParam)) {
            throw AppExceptions.INSTANCE.missingGrantType().exception()
        }

        if (!GrantType.isValid(grantTypeParam)) {
            throw AppExceptions.INSTANCE.invalidGrantType(grantTypeParam).exception()
        }

        GrantType grantType = GrantType.valueOf(grantTypeParam.toUpperCase())

        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        oauthInfo.grantType = grantType

        return true
    }
}
