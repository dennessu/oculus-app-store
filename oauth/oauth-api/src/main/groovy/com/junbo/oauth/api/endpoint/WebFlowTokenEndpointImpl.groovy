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

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MultivaluedMap

/**
 * Javadoc.
 */
@CompileStatic
class WebFlowTokenEndpointImpl implements TokenEndpoint {
    private FlowExecutor flowExecutor
    private String grantTokenFlow

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setGrantTokenFlow(String grantTokenFlow) {
        this.grantTokenFlow = grantTokenFlow
    }

    @Override
    Promise<AccessTokenResponse> postToken(HttpHeaders httpHeaders,
                                           MultivaluedMap<String, String> formParams,
                                           ContainerRequestContext request) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.REQUEST] = request
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams
        requestScope[ActionContextWrapper.HEADER_MAP] = httpHeaders.requestHeaders
        requestScope[ActionContextWrapper.COOKIE_MAP] = httpHeaders.cookies

        flowExecutor.start(grantTokenFlow, requestScope).then { ActionContext context ->
            ActionContextWrapper wrapper = new ActionContextWrapper(context)
            return Promise.pure(wrapper.accessTokenResponse)
        }
    }
}
