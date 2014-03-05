/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.service.AuthenticationService
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.endpoint.AuthenticateEndpoint
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response

/**
 * Javadoc.
 */
@CompileStatic
@Scope('prototype')
class AuthenticateEndpointImpl implements AuthenticateEndpoint {

    private AuthenticationService authenticationService

    @Required
    void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService
    }

    @Override
    Promise<Response> postAuthenticate(HttpHeaders httpHeaders, MultivaluedMap<String, String> formParams,
                                       ContainerRequestContext request) {
        ServiceContext context = new ServiceContext()

        ServiceContextUtil.setParameterMap(context, formParams)
        ServiceContextUtil.setHeaderMap(context, httpHeaders.requestHeaders)
        ServiceContextUtil.setCookieMap(context, httpHeaders.cookies)

        authenticationService.authenticate(context)

        Response.ResponseBuilder responseBuilder = ServiceContextUtil.getResponseBuilder(context)
        List<NewCookie> cookieList = ServiceContextUtil.getResponseCookieList(context)

        cookieList.each { NewCookie cookie ->
            responseBuilder.cookie(cookie)
        }

        Map<String, String> headerMap = ServiceContextUtil.getResponseHeaderMap(context)

        headerMap.each { String key, String value ->
            responseBuilder.header(key, value)
        }

        return Promise.pure(responseBuilder.build())
    }
}
