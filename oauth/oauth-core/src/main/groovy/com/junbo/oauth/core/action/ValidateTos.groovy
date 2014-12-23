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
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ValidateTos.
 */
@CompileStatic
class ValidateTos implements Action {
    private TosUtil tosUtil

    @Required
    void setTosUtil(TosUtil tosUtil) {
        this.tosUtil = tosUtil
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def client = contextWrapper.client
        def user = contextWrapper.user
        assert client != null: 'client is null'
        assert user != null: 'user is null'

        def unacceptedTosIds = tosUtil.getRequiredTos(client, user)

        if (!unacceptedTosIds.isEmpty()) {
            contextWrapper.tosChallenge = []
            unacceptedTosIds.each { TosId id ->
                contextWrapper.tosChallenge.add(id.value)
            }

            return Promise.pure(new ActionResult('tosChallenge'))
        }

        return Promise.pure(null)
    }
}
