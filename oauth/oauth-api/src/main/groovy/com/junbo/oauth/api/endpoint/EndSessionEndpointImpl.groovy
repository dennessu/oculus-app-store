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
 * Default {@link com.junbo.oauth.spec.endpoint.EndSessionEndpoint} implementation.
 * After end session is called, the user's login state, remember me token will be removed from their
 * local browser storage (mostly in the browser cookie).
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.EndSessionEndpoint
 */
@CompileStatic
class EndSessionEndpointImpl implements EndSessionEndpoint {

    /**
     * The flowExecutor to execute the end session flow
     */
    private FlowExecutor flowExecutor

    /**
     * The end session flow name, by default "endSessionFlow"
     */
    private String endSessionFlow

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setEndSessionFlow(String endSessionFlow) {
        this.endSessionFlow = endSessionFlow
    }

    /**
     * Endpoint to end the login session.
     * @param uriInfo The UriInfo contains the path, query parameters.
     * @param httpHeaders The HttpHeaders contains the header information.
     * @param request The raw javax.ws.rs request.
     * @return The raw javax.ws.rs Response.
     */
    @Override
    Promise<Response> endSession(UriInfo uriInfo, HttpHeaders httpHeaders, ContainerRequestContext request) {
        // Prepare the requestScope
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = uriInfo.queryParameters
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.COOKIE_MAP] = httpHeaders.cookies

        // Parse the conversation id and event
        String conversationId = uriInfo.queryParameters.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = uriInfo.queryParameters.getFirst(OAuthParameters.EVENT)?: ''

        // if the conversation id is empty, start a new conversation in the flowExecutor.
        if (StringUtils.isEmpty(conversationId)) {
            return flowExecutor.start(endSessionFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        // else try to resume the conversation with the given conversation id and event in the flowExecutor.
        return flowExecutor.resume(conversationId, event, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }
}
