/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.endpoint.AuthorizeEndpoint
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * WebflowAuthorizeEndpointImpl.
 */
@CompileStatic
class WebflowAuthorizeEndpointImpl implements AuthorizeEndpoint {
    private FlowExecutor flowExecutor
    private String authorizeFlow

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setAuthorizeFlow(String authorizeFlow) {
        this.authorizeFlow = authorizeFlow
    }

    @Override
    Promise<Response> authorize(UriInfo uriInfo, HttpHeaders httpHeaders, ContainerRequestContext request) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = uriInfo.queryParameters
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.COOKIE_MAP] = httpHeaders.cookies

        flowExecutor.start(authorizeFlow, requestScope).then { ActionContext context ->
            ActionContextWrapper wrapper = new ActionContextWrapper(context)
            def responseBuilder = wrapper.responseBuilder
            def cookieList = wrapper.responseCookieList

            cookieList.each { NewCookie cookie ->
                responseBuilder.cookie(cookie)
            }

            def headerMap = wrapper.responseHeaderMap

            headerMap.each { String key, String value ->
                responseBuilder.header(key, value)
            }

            return Promise.pure(responseBuilder.build())
        }
    }
}
