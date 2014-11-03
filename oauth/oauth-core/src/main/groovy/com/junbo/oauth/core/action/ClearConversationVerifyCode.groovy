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

/**
 * ClearConversationVerifyCode.
 */
@CompileStatic
class ClearConversationVerifyCode implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        CookieUtil.clearCookie(context, OAuthParameters.CONVERSATION_VERIFY_CODE)
        return Promise.pure(null)
    }
}
