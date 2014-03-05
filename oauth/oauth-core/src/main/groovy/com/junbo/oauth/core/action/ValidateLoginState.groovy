/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.FlowStateRepository
import com.junbo.oauth.spec.model.FlowState
import com.junbo.oauth.spec.model.Prompt
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.beans.factory.annotation.Required
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.Response

/**
 * Javadoc.
 */
@CompileStatic
class ValidateLoginState implements Action {

    private String loginUri

    private FlowStateRepository flowStateRepository

    @Required
    void setLoginUri(String loginUri) {
        this.loginUri = loginUri
    }

    @Required
    void setFlowStateRepository(FlowStateRepository flowStateRepository) {
        this.flowStateRepository = flowStateRepository
    }

    @Override
    boolean execute(ServiceContext context) {
        def loginState = ServiceContextUtil.getLoginState(context)
        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        def flowState = ServiceContextUtil.getFlowState(context)
        def prompts = oauthInfo.prompts

        if (prompts.contains(Prompt.LOGIN)) {

            if (flowState == null) {
                redirectToLogin(context)
                return false
            }
        } else if (prompts.contains(Prompt.NONE)) {
            if (loginState == null) {
                throw AppExceptions.INSTANCE.loginRequired().exception()
            }
        } else {
            if (loginState == null && flowState == null) {
                redirectToLogin(context)
                return false
            }
        }

        return true
    }

    private void redirectToLogin(ServiceContext context) {
        FlowState flowState = new FlowState()

        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        flowState.oAuthInfo = oauthInfo

        String redirectUri = generateRedirectUri(context)
        flowState.redirectUri = redirectUri

        flowStateRepository.saveOrUpdate(flowState)

        def responseBuilder = Response.status(Response.Status.FOUND).location(generateLoginUri(flowState.id))
        ServiceContextUtil.setResponseBuilder(context, responseBuilder)
    }

    private static String generateRedirectUri(ServiceContext context) {
        def request = (ContainerRequest) ServiceContextUtil.getRequest(context)
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(request.baseUri)
        builder.path(request.getPath(true))
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        parameterMap.each { String key, List<String> values ->
            builder.queryParam(key, values.toArray())
        }

        return builder.build()
    }

    private URI generateLoginUri(String flowStateId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(loginUri)

        uriBuilder.queryParam(OAuthParameters.FLOW_STATE, flowStateId)

        return uriBuilder.build().toUri()
    }
}
