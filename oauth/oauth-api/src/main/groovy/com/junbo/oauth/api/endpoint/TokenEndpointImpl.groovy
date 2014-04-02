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
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Request

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

    /**
     * The HttpHeaders in javax.ws.rs Context
     */
    @Autowired
    private HttpHeaders httpHeaders

    /**
     * The Request in javax.ws.rs Context
     */
    @Autowired
    private Request request

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
    Promise<AccessTokenResponse> postToken(MultivaluedMap<String, String> formParams) {
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

    /**
     * The POST method of the grant token endpoint, with the explicit parameters.
     * @param clientId The client id.
     * @param clientSecret The client secret.
     * @param grantType The grant type ({@link com.junbo.oauth.spec.model.GrantType}).
     * @param code The authorization code.
     * @param scope The scope of the request.
     * @param redirectUri The redirect uri used in the authorization code flow.
     * @param username The username used in the password flow.
     * @param password The password used in the password flow.
     * @param refreshToken The refresh token used in the refresh token flow.
     * @param nonce The nonce used in the openid connect flow.
     * @return The granted access token.
     */
    @Override
    Promise<AccessTokenResponse> postToken(String clientId, String clientSecret, String grantType, String code,
                                           String scope, String redirectUri, String username, String password,
                                           String refreshToken, String nonce) {
        // Prepare the multi valued map
        MultivaluedMap<String, String> formParams = new MultivaluedHashMap<>()

        formParams.putSingle(OAuthParameters.CLIENT_ID, clientId)
        formParams.putSingle(OAuthParameters.CLIENT_SECRET, clientSecret)
        formParams.putSingle(OAuthParameters.GRANT_TYPE, grantType)
        formParams.putSingle(OAuthParameters.CODE, code)
        formParams.putSingle(OAuthParameters.SCOPE, scope)
        formParams.putSingle(OAuthParameters.REDIRECT_URI, redirectUri)
        formParams.putSingle(OAuthParameters.USERNAME, username)
        formParams.putSingle(OAuthParameters.PASSWORD, password)
        formParams.putSingle(OAuthParameters.REFRESH_TOKEN, refreshToken)
        formParams.putSingle(OAuthParameters.NONCE, nonce)

        return postToken(formParams)
    }
}
