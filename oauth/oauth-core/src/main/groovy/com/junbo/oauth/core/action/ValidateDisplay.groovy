/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.Display
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Javadoc.
 */
@CompileStatic
class ValidateDisplay implements Action {
    private Display defaultDisplay

    @Required
    void setDefaultDisplay(Display defaultDisplay) {
        this.defaultDisplay = defaultDisplay
    }

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        String displayParam = parameterMap.getFirst(OAuthParameters.DISPLAY)

        Display display = defaultDisplay
        if (StringUtils.hasText(displayParam)) {
            if (!Display.isValid(displayParam)) {
                throw AppExceptions.INSTANCE.invalidDisplay(displayParam).exception()
            }

            display = Display.valueOf(displayParam.toUpperCase())
        }

        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        oauthInfo.display = display

        return true
    }
}
