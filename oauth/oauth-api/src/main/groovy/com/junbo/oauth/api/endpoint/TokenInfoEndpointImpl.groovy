/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.service.TokenService
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.core.UriInfo

/**
 * Javadoc.
 */
@Component
@CompileStatic
@Scope('prototype')
class TokenInfoEndpointImpl implements TokenInfoEndpoint {
    private TokenService tokenService

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<TokenInfo> getTokenInfo(UriInfo uriInfo) {
        ServiceContext context = new ServiceContext()
        ServiceContextUtil.setParameterMap(context, uriInfo.queryParameters)

        return Promise.pure(tokenService.getAccessTokenInfo(context))
    }
}
