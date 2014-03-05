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
import com.junbo.oauth.spec.endpoint.AuthenticateEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response

/**
 * WebflowAuthenticateEndpointImpl.
 */
@CompileStatic
class WebflowAuthenticateEndpointImpl implements AuthenticateEndpoint {
    private FlowExecutor flowExecutor

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Override
    Promise<Response> postAuthenticate(HttpHeaders httpHeaders, MultivaluedMap<String, String> formParams,
            ContainerRequestContext request) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders

        String conversationId = formParams.getFirst(OAuthParameters.CONVERSATION_ID)

        if (StringUtils.isEmpty(conversationId)) {
            throw AppExceptions.INSTANCE.missingConversationId().exception()
        }

        flowExecutor.resume(conversationId, "login", requestScope).then { ActionContext context ->
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
