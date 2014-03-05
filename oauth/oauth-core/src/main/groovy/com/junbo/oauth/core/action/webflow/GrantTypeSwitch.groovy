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
import com.junbo.oauth.spec.model.GrantType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * GrantTypeSwitch.
 */
@CompileStatic
class GrantTypeSwitch implements Action {

    private Map<GrantType, Action> actionTypeMap

    @Required
    void setActionTypeMap(Map<GrantType, Action> actionTypeMap) {
        this.actionTypeMap = actionTypeMap
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def action = actionTypeMap.get(oauthInfo.grantType)
        return action.execute(context)
    }
}
