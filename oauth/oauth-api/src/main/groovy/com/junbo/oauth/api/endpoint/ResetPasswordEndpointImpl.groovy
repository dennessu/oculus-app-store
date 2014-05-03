package com.junbo.oauth.api.endpoint

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.ConversationNotfFoundException
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.core.util.ResponseUtil
import com.junbo.oauth.spec.endpoint.ResetPasswordEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
@Scope('prototype')
class ResetPasswordEndpointImpl implements ResetPasswordEndpoint {
    private FlowExecutor flowExecutor
    private String resetPasswordFlow
    private UserService userService

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setResetPasswordFlow(String resetPasswordFlow) {
        this.resetPasswordFlow = resetPasswordFlow
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<Response> resetPasswordStart(String code, String locale, String cid) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[OAuthParameters.RESET_PASSWORD_CODE] = code
        requestScope[OAuthParameters.LOCALE] = locale

        if (cid == null) {
            //start a new conversation in the flowExecutor.
            return flowExecutor.start(resetPasswordFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        return flowExecutor.resume(cid, '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    @Override
    Promise<Response> resetPassword(String conversationId, String event, MultivaluedMap<String, String> formParams) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams

        if (conversationId == null) {
            throw new ConversationNotfFoundException()
        }

        return flowExecutor.resume(conversationId, event ?: '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    @Override
    Promise<Response> sendResetPasswordEmail(String authorization, String locale, UserId userId, ContainerRequestContext request) {
        return userService.resetPasswordByAuthHeader(authorization, userId, locale, ((ContainerRequest)request).baseUri).then {
            return Promise.pure(Response.noContent().build())
        }
    }
}
