/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.GroupUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * ValidateClientAfterLogin.
 */
@CompileStatic
class ValidateClientAfterLogin implements Action {
    private boolean isAuthorizeFlow

    private GroupUtil groupUtil

    @Required
    void setIsAuthorizeFlow(boolean isAuthorizeFlow) {
        this.isAuthorizeFlow = isAuthorizeFlow
    }

    @Required
    void setGroupUtil(GroupUtil groupUtil) {
        this.groupUtil = groupUtil
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def oauthInfo = contextWrapper.oauthInfo
        def loginState = contextWrapper.loginState
        def client = contextWrapper.client

        Assert.notNull(oauthInfo, 'oauthInfo is null')
        Assert.notNull(client, 'client is null')

        if (loginState != null && client.allowedGroups != null && !client.allowedGroups.isEmpty()) {
            Boolean allowed = groupUtil.hasGroups(new UserId(loginState.userId), client.allowedGroups.toArray(new String[0]))
            if (allowed) {
                return Promise.pure(new ActionResult('success'))
            } else {
                if (!isAuthorizeFlow) {
                    throw AppCommonErrors.INSTANCE
                            .forbiddenWithMessage("The user does not match the " +
                            "pre-condition for this client").exception()
                } else {
                    contextWrapper.errors.add(AppCommonErrors.INSTANCE.
                            forbiddenWithMessage("The user does not match the " +
                                    "pre-condition for this client").error())
                    return Promise.pure(new ActionResult('forbidden'))
                }
            }
        }

        return Promise.pure(new ActionResult('success'))
    }
}
