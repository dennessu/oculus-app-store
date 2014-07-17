package com.junbo.oauth.api.endpoint

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.ConversationNotfFoundException
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
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
    private String forgetPasswordFlow
    private UserService userService
    private boolean debugEnabled

    @Required
    void setFlowExecutor(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor
    }

    @Required
    void setResetPasswordFlow(String resetPasswordFlow) {
        this.resetPasswordFlow = resetPasswordFlow
    }

    @Required
    void setForgetPasswordFlow(String forgetPasswordFlow) {
        this.forgetPasswordFlow = forgetPasswordFlow
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled
    }

    @Override
    Promise<Response> forgetPassword(String cid, String locale) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[OAuthParameters.LOCALE] = locale

        if (cid == null) {
            //start a new conversation in the flowExecutor.
            return flowExecutor.start(forgetPasswordFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        return flowExecutor.resume(cid, '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    @Override
    Promise<Response> forgetPassword(String conversationId, String event, MultivaluedMap<String, String> formParams) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams

        if (conversationId == null) {
            throw new ConversationNotfFoundException()
        }

        return flowExecutor.resume(conversationId, event ?: '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    @Override
    Promise<Response> resetPasswordLink(String cid, String code, String locale, String country) {
        Map<String, Object> requestScope = new HashMap<>()
        requestScope[OAuthParameters.RESET_PASSWORD_CODE] = code
        requestScope[OAuthParameters.LOCALE] = locale
        requestScope[OAuthParameters.COUNTRY] = country

        if (cid == null) {
            //start a new conversation in the flowExecutor.
            return flowExecutor.start(resetPasswordFlow, requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
        }

        return flowExecutor.resume(cid, '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    @Override
    Promise<Response> resetPassword(String conversationId, String event, String locale, String country, String username, String userEmail,
                                    ContainerRequestContext request, MultivaluedMap<String, String> formParams) {
        if (conversationId == null) {
            if (username != null) {
                return userService.getUserIdByUsername(username).then { UserId id ->
                    return userService.sendResetPassword(id, locale, country, ((ContainerRequest)request).baseUri).then { String uri ->
                        if (debugEnabled) {
                            return Promise.pure(Response.ok().entity(uri).build())
                        }

                        return Promise.pure(Response.noContent().build())
                    }
                }
            }
            else if (userEmail != null) {
                return userService.getUserIdByUserEmail(userEmail).then { UserId id ->
                    return userService.sendResetPassword(id, locale, country, ((ContainerRequest)request).baseUri).then { String uri ->
                        if (debugEnabled) {
                            return Promise.pure(Response.ok().entity(uri).build())
                        }

                        return Promise.pure(Response.noContent().build())
                    }
                }
            }
            else {
                throw AppExceptions.INSTANCE.missingUsernameOrUserEmail().exception()
            }
        }

        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams

        return flowExecutor.resume(conversationId, event ?: '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }
}
