/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.service.AuthorizationService
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.spec.endpoint.AuthorizeEndpoint
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * Javadoc.
 */
@Component
@CompileStatic
@Scope('prototype')
class AuthorizeEndpointImpl implements AuthorizeEndpoint {

    private AuthorizationService authorizationService

    @Required
    void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService
    }

    @Override
    Promise<Response> authorize(UriInfo uriInfo, HttpHeaders httpHeaders, ContainerRequestContext request) {
        ServiceContext context = new ServiceContext()
        ServiceContextUtil.setRequest(context, request)
        ServiceContextUtil.setParameterMap(context, uriInfo.queryParameters)
        ServiceContextUtil.setHeaderMap(context, httpHeaders.requestHeaders)
        ServiceContextUtil.setCookieMap(context, httpHeaders.cookies)

        authorizationService.authorize(context)

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
