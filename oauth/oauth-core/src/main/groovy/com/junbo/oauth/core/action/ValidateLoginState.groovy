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
import com.junbo.oauth.spec.model.Prompt
import groovy.transform.CompileStatic

/**
 * ValidateLoginState.
 */
@CompileStatic
class ValidateLoginState implements Action {

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def loginState = contextWrapper.loginState
        def oauthInfo = contextWrapper.oauthInfo
        def prompts = oauthInfo.prompts

        if (prompts.contains(Prompt.LOGIN)) {
            return Promise.pure(new ActionResult('loginRequired'))
        } else if (prompts.contains(Prompt.NONE)) {
            if (loginState == null) {
                return Promise.pure(new ActionResult('loginRequiredError'))
            }
        } else if (oauthInfo.maxAge != null
                && loginState.lastAuthDate.time + oauthInfo.maxAge * 1000 < System.currentTimeMillis()) {
            return Promise.pure(new ActionResult('loginRequired'))
        } else {
            if (loginState == null) {
                return Promise.pure(new ActionResult('loginRequired'))
            }
        }

        return Promise.pure(null)
    }
}
