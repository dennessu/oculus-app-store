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
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * ValidateMaxAge.
 */
@CompileStatic
class ValidateMaxAge implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap

        String maxAgeStr = parameterMap.getFirst(OAuthParameters.MAX_AGE)

        if (!StringUtils.hasText(maxAgeStr)) {
            return Promise.pure(null)
        }

        Long maxAge = Long.MAX_VALUE
        try {
            maxAge = Long.parseLong(maxAgeStr)
        } catch (NumberFormatException e) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('max_age').exception()
        }

        def oauthInfo = contextWrapper.oauthInfo
        oauthInfo.maxAge = maxAge

        return Promise.pure(null)
    }
}
