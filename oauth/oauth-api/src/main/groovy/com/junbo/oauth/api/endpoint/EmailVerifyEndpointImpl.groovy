/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.util.ResponseUtil
import com.junbo.oauth.spec.endpoint.EmailVerifyEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.util.StringUtils

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

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setEmailVerifyFlow(String emailVerifyFlow) {
        this.emailVerifyFlow = emailVerifyFlow
    }

    @Override
    Promise<Response> verifyEmail(String code, String locale, String conversationId, String event) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[OAuthParameters.CODE] = code
        requestScope[OAuthParameters.LOCALE] = locale

        // if the conversation id is empty, start a new conversation in the flowExecutor.
        if (StringUtils.isEmpty(conversationId)) {
            return flowExecutor.start(emailVerifyFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        // else try to resume the conversation with the given conversation id and event in the flowExecutor.
        return flowExecutor.resume(conversationId, event ?: '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }
}
