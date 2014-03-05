/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.GrantType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class GrantTypeSwitch implements Action {
    private Map<GrantType, Action> actionTypeMap

    @Required
    void setActionTypeMap(Map<GrantType, Action> actionTypeMap) {
        this.actionTypeMap = actionTypeMap
    }

    @Override
    boolean execute(ServiceContext context) {
        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        Action action = actionTypeMap.get(oauthInfo.grantType)
        return action.execute(context)
    }
}
