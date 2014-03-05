/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.action.Action
import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.service.AuthorizationService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class AuthorizationServiceImpl implements AuthorizationService {

    private Action authorizationAction

    @Required
    void setAuthorizationAction(Action authorizationAction) {
        this.authorizationAction = authorizationAction
    }

    @Override
    void authorize(ServiceContext context) {
        authorizationAction.execute(context)
    }
}
