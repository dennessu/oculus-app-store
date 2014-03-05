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
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.model.AccessTokenResponse
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MultivaluedMap

/**
 * Javadoc.
 */
@Component
@CompileStatic
@Scope('prototype')
class TokenEndpointImpl implements TokenEndpoint {

    private TokenService tokenService

    @Required
    void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    Promise<AccessTokenResponse> postToken(HttpHeaders httpHeaders, MultivaluedMap<String, String> formParams,
                                           ContainerRequestContext request) {
        ServiceContext context = new ServiceContext()

        ServiceContextUtil.setParameterMap(context, formParams)
        ServiceContextUtil.setHeaderMap(context, httpHeaders.requestHeaders)
        ServiceContextUtil.setCookieMap(context, httpHeaders.cookies)
        ServiceContextUtil.setRequest(context, request)

        tokenService.grantAccessToken(context)

        return Promise.pure(ServiceContextUtil.getAccessTokenResponse(context))
    }
}
