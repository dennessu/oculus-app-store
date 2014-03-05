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
import com.junbo.oauth.spec.model.Prompt
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.web.util.UriComponentsBuilder

/**
 * ValidateLoginState.
 */
@CompileStatic
class ValidateLoginState implements Action {

    private String loginUri

    @Required
    void setLoginUri(String loginUri) {
        this.loginUri = loginUri
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def loginState = contextWrapper.loginState
        def oauthInfo = contextWrapper.oauthInfo
        def prompts = oauthInfo.prompts

        if (prompts.contains(Prompt.LOGIN)) {
            redirectToLogin(contextWrapper)
            return Promise.pure(new ActionResult('redirectLogin'))
        } else if (prompts.contains(Prompt.NONE)) {
            if (loginState == null) {
                throw AppExceptions.INSTANCE.loginRequired().exception()
            }
        } else if (oauthInfo.maxAge != null
                && loginState.lastAuthDate.time + oauthInfo.maxAge * 1000 < System.currentTimeMillis()) {
            redirectToLogin(contextWrapper)
            return Promise.pure(new ActionResult('redirectLogin'))
        } else {
            if (loginState == null) {
                redirectToLogin(contextWrapper)
                return Promise.pure(new ActionResult('redirectLogin'))
            }
        }

        return Promise.pure(null)
    }

    private void redirectToLogin(ActionContextWrapper context) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(loginUri)

        uriBuilder.queryParam(OAuthParameters.CONVERSATION_ID, context.conversationId)
        uriBuilder.queryParam(OAuthParameters.EVENT, 'login')

        context.redirectUriBuilder = uriBuilder
    }

}
