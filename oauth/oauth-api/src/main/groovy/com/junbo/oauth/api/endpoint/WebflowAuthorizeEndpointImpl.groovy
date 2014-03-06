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
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.endpoint.AuthorizeEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.*

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

    private final static Closure WRITE_RESPONSE_CLOSURE = { ActionContext context ->
        ActionContextWrapper wrapper = new ActionContextWrapper(context)
        def responseBuilder = wrapper.responseBuilder
        def cookieList = wrapper.responseCookieList
        def headerMap = wrapper.responseHeaderMap

        cookieList.each { NewCookie cookie ->
            responseBuilder.cookie(cookie)
        }

        headerMap.each { String key, String value ->
            responseBuilder.header(key, value)
        }

        return Promise.pure(responseBuilder.build())
    }

    @Override
    Promise<Response> authorize(UriInfo uriInfo, HttpHeaders httpHeaders, ContainerRequestContext request) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = uriInfo.queryParameters
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.COOKIE_MAP] = httpHeaders.cookies

        String conversationId = uriInfo.queryParameters.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = uriInfo.queryParameters.getFirst(OAuthParameters.EVENT)

        if (StringUtils.isEmpty(conversationId)) {
            flowExecutor.start(authorizeFlow, requestScope).then(WRITE_RESPONSE_CLOSURE)
        } else {
            flowExecutor.resume(conversationId, event, requestScope).then(WRITE_RESPONSE_CLOSURE)
        }
    }

    @Override
    Promise<Response> postAuthorize(HttpHeaders httpHeaders, MultivaluedMap<String, String> formParams,
                                    ContainerRequestContext request) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders

        String conversationId = formParams.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = formParams.getFirst(OAuthParameters.EVENT)

        if (StringUtils.isEmpty(conversationId)) {
            throw AppExceptions.INSTANCE.missingConversationId().exception()
        }

        flowExecutor.resume(conversationId, event, requestScope).then(WRITE_RESPONSE_CLOSURE)
    }
}
