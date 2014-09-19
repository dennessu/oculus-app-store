/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.model.AccessTokenRequest
import com.junbo.oauth.spec.model.AccessTokenResponse
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Component

import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap
/**
 * Default {@link com.junbo.oauth.spec.endpoint.TokenEndpoint} implementation.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.TokenEndpoint
 */
@Component
@CompileStatic
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
    Promise<AccessTokenResponse> postToken(AccessTokenRequest request) {
        // Prepare the request scope.
        Map<String, Object> requestScope = new HashMap<>()

        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<>()

        formParams.putSingle(OAuthParameters.CLIENT_ID, request.clientId)
        formParams.putSingle(OAuthParameters.CLIENT_SECRET, request.clientSecret)
        formParams.putSingle(OAuthParameters.GRANT_TYPE, request.grantType)
        formParams.putSingle(OAuthParameters.CODE, request.code)
        formParams.putSingle(OAuthParameters.SCOPE, request.scope)
        formParams.putSingle(OAuthParameters.REDIRECT_URI, request.redirectUri)
        formParams.putSingle(OAuthParameters.USERNAME, request.username)
        formParams.putSingle(OAuthParameters.PASSWORD, request.password)
        formParams.putSingle(OAuthParameters.REFRESH_TOKEN, request.refreshToken)
        formParams.putSingle(OAuthParameters.NONCE, request.nonce)
        formParams.putSingle(OAuthParameters.IP_RESTRICTION, request.ipRestriction)
        formParams.putSingle(OAuthParameters.USER_ID, request.userId)
        formParams.putSingle(OAuthParameters.ACCESS_TOKEN, request.accessToken)

        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams

        requestScope[ActionContextWrapper.REMOTE_ADDRESS] = JunboHttpContext.requestIpAddress

        requestScope[ActionContextWrapper.HEADER_MAP] = JunboHttpContext.requestHeaders

        // Start a new conversation of grantTokenFlow in the flowExecutor.
        return flowExecutor.start(grantTokenFlow, requestScope).then { ActionContext context ->
            ActionContextWrapper wrapper = new ActionContextWrapper(context)
            return Promise.pure(wrapper.accessTokenResponse)
        }
    }
}
