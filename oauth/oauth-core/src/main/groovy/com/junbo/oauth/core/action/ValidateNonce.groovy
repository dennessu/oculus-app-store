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

        String state = parameterMap.getFirst(OAuthParameters.STATE)

        if (OAuthInfoUtil.isImplicitFlow(oauthInfo)) {
            if (state == null) {
                throw AppCommonErrors.INSTANCE.parameterRequired('state').exception()
            }
        }

        oauthInfo.state = state


        String nonce = parameterMap.getFirst(OAuthParameters.NONCE)

        if (OAuthInfoUtil.isImplicitFlow(oauthInfo) && OAuthInfoUtil.isIdTokenNeeded(oauthInfo)) {
            if (nonce == null) {
                throw AppCommonErrors.INSTANCE.parameterRequired('nonce').exception()
            }
        }

        oauthInfo.nonce = nonce

        return Promise.pure(null)
    }
}
