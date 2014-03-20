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
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic

/**
 * ValidateNonce.
 */
@CompileStatic
class ValidateNonce implements Action {

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def oauthInfo = contextWrapper.oauthInfo

        if (OAuthInfoUtil.isOpenIdConnect(oauthInfo)) {
            String nonce = parameterMap.getFirst(OAuthParameters.NONCE)
            if (nonce == null && OAuthInfoUtil.isImplicitFlow(oauthInfo)) {
                throw AppExceptions.INSTANCE.missingNonce().exception()
            }

            oauthInfo.nonce = nonce
        }

        return Promise.pure(null)
    }
}
