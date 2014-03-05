/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ActionChain.
 */
@CompileStatic
class ActionChain implements Action {
    private List<Action> actionList

    @Required
    void setActionList(List<Action> actionList) {
        this.actionList = actionList
    }

    @Override
    boolean execute(ServiceContext context) {
        for (Action action : actionList) {
            if (!action.execute(context)) {
                return false
            }
        }
        return true
    }
}
