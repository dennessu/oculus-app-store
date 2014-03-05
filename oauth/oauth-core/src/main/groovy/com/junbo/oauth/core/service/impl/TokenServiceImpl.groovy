/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.action.Action
import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class TokenServiceImpl implements TokenService {
    private Action grantTokenAction
    private Action getTokenInfoAction

    @Required
    void setGrantTokenAction(Action grantTokenAction) {
        this.grantTokenAction = grantTokenAction
    }

    @Required
    void setGetTokenInfoAction(Action getTokenInfoAction) {
        this.getTokenInfoAction = getTokenInfoAction
    }

    @Override
    void grantAccessToken(ServiceContext context) {
        grantTokenAction.execute(context)
    }

    @Override
    TokenInfo getAccessTokenInfo(ServiceContext context) {
        getTokenInfoAction.execute(context)
        return ServiceContextUtil.getTokenInfo(context)
    }
}
