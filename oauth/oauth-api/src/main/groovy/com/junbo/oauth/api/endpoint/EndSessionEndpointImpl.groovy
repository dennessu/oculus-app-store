/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.ResponseUtil
import com.junbo.oauth.spec.endpoint.EndSessionEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.util.StringUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * EndSessionEndpointImpl.
 */
@CompileStatic
@Scope('prototype')
class EndSessionEndpointImpl implements EndSessionEndpoint {

    private FlowExecutor flowExecutor
    private String endSessionFlow

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setEndSessionFlow(String endSessionFlow) {
        this.endSessionFlow = endSessionFlow
    }

    @Override
    Promise<Response> endSession(UriInfo uriInfo, HttpHeaders httpHeaders, ContainerRequestContext request) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = uriInfo.queryParameters
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.COOKIE_MAP] = httpHeaders.cookies

        String conversationId = uriInfo.queryParameters.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = uriInfo.queryParameters.getFirst(OAuthParameters.EVENT)

        if (StringUtils.isEmpty(conversationId)) {
            return flowExecutor.start(endSessionFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        return flowExecutor.resume(conversationId, event, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }
}
