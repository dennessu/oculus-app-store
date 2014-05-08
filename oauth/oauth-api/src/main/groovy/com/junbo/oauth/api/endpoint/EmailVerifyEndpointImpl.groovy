/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.core.util.ResponseUtil
import com.junbo.oauth.spec.endpoint.EmailVerifyEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.util.StringUtils

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response

/**
 * EmailVerifyEndpointImpl.
 */
@CompileStatic
@Scope('prototype')
class EmailVerifyEndpointImpl implements EmailVerifyEndpoint {
    /**
     * The flowExecutor to execute the authorize flow
     */
    private FlowExecutor flowExecutor

    /**
     * The emailVerifyFlow name, by default "emailVerifyFlow"
     */
    private String emailVerifyFlow

    private UserService userService

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setEmailVerifyFlow(String emailVerifyFlow) {
        this.emailVerifyFlow = emailVerifyFlow
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<Response> verifyEmail(String evc, String locale, String conversationId, String event) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[OAuthParameters.EMAIL_VERIFY_CODE] = evc
        requestScope[OAuthParameters.LOCALE] = locale

        // if the conversation id is empty, start a new conversation in the flowExecutor.
        if (StringUtils.isEmpty(conversationId)) {
            return flowExecutor.start(emailVerifyFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        // else try to resume the conversation with the given conversation id and event in the flowExecutor.
        return flowExecutor.resume(conversationId, event ?: '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    @Override
    Promise<Response> sendVerifyEmailEmail(String authorization, String locale, ContainerRequestContext request) {
        return userService.verifyEmailByAuthHeader(authorization, locale, ((ContainerRequest)request).baseUri).then {
            return Promise.pure(Response.noContent().build())
        }
    }
}
