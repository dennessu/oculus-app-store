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
import com.junbo.oauth.core.util.CookieUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * GenerateConversationVerifyCode.
 */
@CompileStatic
class GenerateConversationVerifyCode implements Action {

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        if (StringUtils.isEmpty(context.conversationVerifyCode)) {
            context.conversationVerifyCode = UUID.randomUUID().toString()
            CookieUtil.setCookie(context, OAuthParameters.CONVERSATION_VERIFY_CODE, context.conversationVerifyCode, 300)
        }

        return Promise.pure(null)
    }
}
