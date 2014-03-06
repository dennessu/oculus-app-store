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
import com.junbo.oauth.spec.model.GrantType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * ValidateGrantType.
 */
@CompileStatic
class ValidateGrantType implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String grantTypeParam = parameterMap.getFirst(OAuthParameters.GRANT_TYPE)

        if (!StringUtils.hasText(grantTypeParam)) {
            throw AppExceptions.INSTANCE.missingGrantType().exception()
        }

        if (!GrantType.isValid(grantTypeParam)) {
            throw AppExceptions.INSTANCE.invalidGrantType(grantTypeParam).exception()
        }

        GrantType grantType = GrantType.valueOf(grantTypeParam.toUpperCase())

        def oauthInfo = contextWrapper.oauthInfo
        oauthInfo.grantType = grantType

        return Promise.pure(new ActionResult(grantType.name()))
    }
}
