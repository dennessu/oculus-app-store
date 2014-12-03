/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.id.TosId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.TosUtil
import com.junbo.oauth.spec.model.Prompt
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ValidateLoginState.
 */
@CompileStatic
class ValidateLoginState implements Action {
    private TosUtil tosUtil

    @Required
    void setTosUtil(TosUtil tosUtil) {
        this.tosUtil = tosUtil
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
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
            def event = parameterMap.getFirst(OAuthParameters.EVENT)
            if (event != null && event == 'register') {
                return Promise.pure(new ActionResult('register'))
            }

            if (loginState == null) {
                return Promise.pure(new ActionResult('loginRequired'))
            }

            def user = contextWrapper.user
            def client = contextWrapper.client
            assert user != null : 'user is null'
            assert client != null : 'client is null'

            Set<TosId> tosChallenge = tosUtil.getRequiredTos(client, user)

            Set<String> tosChallengeStr = []
            tosChallenge.each { TosId tosId ->
                tosChallengeStr.add(tosId.value)
            }
            contextWrapper.tosChallenge = tosChallengeStr

            if (!tosChallenge.isEmpty()) {
                if (prompts.contains(Prompt.TOS)) {
                    return Promise.pure(new ActionResult('tosChallenge'))
                } else {
                    return Promise.pure(new ActionResult('tosChallengeRequired'))
                }
            }
        }

        return Promise.pure(null)
    }
}
