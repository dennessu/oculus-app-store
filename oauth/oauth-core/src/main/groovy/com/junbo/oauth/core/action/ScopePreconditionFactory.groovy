/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.id.UserId
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.GroupUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ScopePreconditionFactory.
 */
@CompileStatic
class ScopePreconditionFactory {
    private GroupUtil groupUtil

    @Required
    void setGroupUtil(GroupUtil groupUtil) {
        this.groupUtil = groupUtil
    }

    ScopePrecondition create(ActionContextWrapper context) {
        return new ScopePrecondition(this, context)
    }

    public static class ScopePrecondition {
        private ActionContextWrapper context
        private ScopePreconditionFactory factory

        ScopePrecondition(ScopePreconditionFactory factory, ActionContextWrapper context) {
            this.factory = factory
            this.context = context
        }

        Boolean hasGroups(String... groups) {
            def loginState = context.loginState
            assert loginState != null : 'loginState is null'
            UserId userId = new UserId(loginState.userId)

            return factory.groupUtil.hasGroups(userId, groups)
        }
    }
}
