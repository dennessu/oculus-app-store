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
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class ValidateMaxAge implements Action {
    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        String maxAgeStr = parameterMap.getFirst(OAuthParameters.MAX_AGE)

        if (!StringUtils.hasText(maxAgeStr)) {
            return true
        }

        Long maxAge = Long.MAX_VALUE
        try {
            maxAge = Long.parseLong(maxAgeStr)
        } catch (NumberFormatException e) {
            throw AppExceptions.INSTANCE.invalidMaxAge(maxAgeStr).exception()
        }

        def loginState = ServiceContextUtil.getLoginState(context)

        if (loginState.lastAuthDate.time + maxAge * 1000 < System.currentTimeMillis()) {
            ServiceContextUtil.setLoginState(context, null)
        }

        return true
    }
}
