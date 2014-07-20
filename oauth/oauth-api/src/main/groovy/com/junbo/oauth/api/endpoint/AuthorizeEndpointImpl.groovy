/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
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
 * Default {@link com.junbo.oauth.spec.endpoint.AuthorizeEndpoint} implementation.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.AuthorizeEndpoint
 */
@Component
@CompileStatic
@Scope('prototype')
class AuthorizeEndpointImpl implements AuthorizeEndpoint {

    /**
     * The flowExecutor to execute the authorize flow
     */
    private FlowExecutor flowExecutor

    /**
     * The authorize flow name, by default "authorizeFlow"
     */
    private String authorizeFlow

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setAuthorizeFlow(String authorizeFlow) {
        this.authorizeFlow = authorizeFlow
    }

    /**
     * The GET method of the authorize endpoint.
     * @param uriInfo The UriInfo contains the path, query parameters
     * @param httpHeaders The HttpHeaders contains the header information
     * @param request The raw javax.ws.rs request
     * @return The raw javax.ws.rs response (mostly http response with response code of 302 Found)
     */
    @Override
    Promise<Response> authorize(UriInfo uriInfo, HttpHeaders httpHeaders, ContainerRequestContext request) {
        // Prepare the requestScope.
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = uriInfo.queryParameters
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.COOKIE_MAP] = httpHeaders.cookies
        requestScope[ActionContextWrapper.REMOTE_ADDRESS] = JunboHttpContext.requestIpAddress

        // Parse the conversation id and event.
        String conversationId = uriInfo.queryParameters.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = uriInfo.queryParameters.getFirst(OAuthParameters.EVENT)

        // GET method is not allowed if sensitive data is provided. (no query parameter except event is allowed)
        if (StringUtils.hasText(event) && uriInfo.queryParameters.size() > 1) {
            throw AppErrors.INSTANCE.methodNotAllowed().exception()
        }

        event = ''
        // if the conversation id is empty, start a new conversation in the flowExecutor.
        if (StringUtils.isEmpty(conversationId)) {
            return flowExecutor.start(authorizeFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        // else try to resume the conversation with the given conversation id and event in the flowExecutor.
        return flowExecutor.resume(conversationId, event, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    /**
     * The POST method of the authorize endpoint.
     * Used when additional parameters such as username or password are needed in the middle of the conversation.
     * @param httpHeaders The HttpHeaders contains the header information.
     * @param formParams The form parameters encoded in format of application/x-www-form-urlencoded.
     * @param request The raw javax.ws.rs request.
     * @return The raw javax.ws.rs response.
     */
    @Override
    Promise<Response> postAuthorize(HttpHeaders httpHeaders, MultivaluedMap<String, String> formParams,
                                    ContainerRequestContext request) {
        // Prepare the requestScope.
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.REMOTE_ADDRESS] = JunboHttpContext.requestIpAddress

        // Parse the conversation id and event.
        String conversationId = formParams.getFirst(OAuthParameters.CONVERSATION_ID)
        String event = formParams.getFirst(OAuthParameters.EVENT) ?: ''

        // The post authorize flow will always be in one on-going conversation, conversation id should not be empty.
        if (StringUtils.isEmpty(conversationId)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('conversationId').exception()
        }

        // try to resume the conversation with the given conversation id and event in the flowExecutor
        return flowExecutor.resume(conversationId, event, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }
}
