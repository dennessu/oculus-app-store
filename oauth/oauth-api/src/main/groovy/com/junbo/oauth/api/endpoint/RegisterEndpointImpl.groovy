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
import com.junbo.oauth.spec.endpoint.RegisterEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.*

/**
 * RegisterEndpointImpl.
 */
@CompileStatic
class RegisterEndpointImpl implements RegisterEndpoint {
    private FlowExecutor flowExecutor

    private String registerFlow

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setRegisterFlow(String registerFlow) {
        this.registerFlow = registerFlow
    }

    @Override
    Promise<Response> register(
            @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders, @Context ContainerRequestContext request) {
        // Prepare the requestScope.
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = uriInfo.queryParameters
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.COOKIE_MAP] = httpHeaders.cookies

        // Parse the conversation id and event.
        String conversationId = uriInfo.queryParameters.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = uriInfo.queryParameters.getFirst(OAuthParameters.EVENT) ?: ''

        // if the conversation id is empty, start a new conversation in the flowExecutor.
        if (StringUtils.isEmpty(conversationId)) {
            return flowExecutor.start(registerFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        // else try to resume the conversation with the given conversation id and event in the flowExecutor.
        return flowExecutor.resume(conversationId, event, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    @Override
    Promise<Response> postRegister(
            @Context HttpHeaders httpHeaders, MultivaluedMap<String, String> formParams,
            @Context ContainerRequestContext request) {
        // Prepare the requestScope.
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders

        // Parse the conversation id and event.
        String conversationId = formParams.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = formParams.getFirst(OAuthParameters.EVENT) ?: ''

        // The post authorize flow will always be in one on-going conversation, conversation id should not be empty.
        if (StringUtils.isEmpty(conversationId)) {
            throw AppExceptions.INSTANCE.missingConversationId().exception()
        }

        // try to resume the conversation with the given conversation id and event in the flowExecutor
        return flowExecutor.resume(conversationId, event, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }
}
