/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ResponseUtil
import com.junbo.oauth.spec.endpoint.AuthorizeEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * Javadoc.
 */
@Component
@CompileStatic
@Scope('prototype')
class AuthorizeEndpointImpl implements AuthorizeEndpoint {

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

        String conversationId = uriInfo.queryParameters.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = uriInfo.queryParameters.getFirst(OAuthParameters.EVENT)

        if (StringUtils.isEmpty(conversationId)) {
            return flowExecutor.start(authorizeFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        return flowExecutor.resume(conversationId, event, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
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

        return flowExecutor.resume(conversationId, event, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }
}
