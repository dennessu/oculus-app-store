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
import com.junbo.oauth.spec.model.Display
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * ValidateDisplay.
 */
@CompileStatic
class ValidateDisplay implements Action {

    private Display defaultDisplay

    @Required
    void setDefaultDisplay(Display defaultDisplay) {
        this.defaultDisplay = defaultDisplay
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String displayParam = parameterMap.getFirst(OAuthParameters.DISPLAY)

        Display display = defaultDisplay
        if (StringUtils.hasText(displayParam)) {
            if (!Display.isValid(displayParam)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('display', displayParam).exception()
            }

            display = Display.valueOf(displayParam.toUpperCase())
        }

        def oauthInfo = contextWrapper.oauthInfo
        oauthInfo.display = display

        return Promise.pure(null)
    }
}
