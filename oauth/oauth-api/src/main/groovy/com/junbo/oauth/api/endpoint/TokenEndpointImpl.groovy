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
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.model.AccessTokenResponse
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MultivaluedMap

/**
 * Default {@link com.junbo.oauth.spec.endpoint.TokenEndpoint} implementation.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.TokenEndpoint
 */
@Component
@CompileStatic
@Scope('prototype')
class TokenEndpointImpl implements TokenEndpoint {
    /**
     * The flowExecutor to execute the token flow
     */
    private FlowExecutor flowExecutor

    /**
     * The token flow name, by default 'grantTokenFlow'.
     */
    private String grantTokenFlow

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setGrantTokenFlow(String grantTokenFlow) {
        this.grantTokenFlow = grantTokenFlow
    }

    /**
     * The POST method of the grant token endpoint.
     * @param httpHeaders The HttpHeaders contains the header information.
     * @param formParams The form parameters encoded in format of application/x-www-form-urlencoded.
     * @param request The raw javax.ws.rs Request
     * @return The granted access token.
     */
    @Override
    Promise<AccessTokenResponse> postToken(HttpHeaders httpHeaders,
                                           MultivaluedMap<String, String> formParams,
                                           ContainerRequestContext request) {
        // Prepare the request scope.
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.COOKIE_MAP] = httpHeaders.cookies

        // Start a new conversation of grantTokenFlow in the flowExecutor.
        flowExecutor.start(grantTokenFlow, requestScope).then { ActionContext context ->
            ActionContextWrapper wrapper = new ActionContextWrapper(context)
            return Promise.pure(wrapper.accessTokenResponse)
        }
    }
}
