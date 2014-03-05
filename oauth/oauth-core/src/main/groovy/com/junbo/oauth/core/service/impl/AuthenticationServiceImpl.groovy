/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.action.Action
import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.service.AuthenticationService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class AuthenticationServiceImpl implements AuthenticationService {
    private Action authenticateAction

    @Required
    void setAuthenticateAction(Action authenticateAction) {
        this.authenticateAction = authenticateAction
    }

    @Override
    void authenticate(ServiceContext context) {
        authenticateAction.execute(context)
    }
}
