package com.junbo.oauth.api.endpoint

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.ConversationNotfFoundException
import com.junbo.langur.core.webflow.executor.FlowExecutor
import com.junbo.oauth.common.Utils
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.core.util.ResponseUtil
import com.junbo.oauth.spec.endpoint.ResetPasswordEndpoint
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ResetPasswordEndpointImpl implements ResetPasswordEndpoint {
    private FlowExecutor flowExecutor
    private String resetPasswordFlow
    private String forgetPasswordFlow
    private UserService userService
    private CsrLogResource csrLogResource
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
    void setCsrLogResource(CsrLogResource csrLogResource) {
        this.csrLogResource = csrLogResource
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
    Promise<Response> resetPassword(String conversationId, String event, String locale, String country,
                                    String username, String userEmail, MultivaluedMap<String, String> formParams) {
        if (conversationId == null) {
            if (username != null) {
                return userService.getUserIdByUsername(username).then { UserId id ->
                    return userService.sendResetPassword(id, locale, country).then { String uri ->
                        csrActionAudit(id)
                        if (debugEnabled || AuthorizeContext.debugEnabled) {
                            return Promise.pure(Response.ok().entity(uri).build())
                        }

                        return userService.getUserEmailByUserId(id).then { String email ->
                            Promise.pure(Response.ok().entity(Utils.maskEmail(email)).build())
                        }
                    }
                }
            }
            else if (userEmail != null) {
                return userService.getUserIdByUserEmail(userEmail).then { UserId id ->
                    return userService.sendResetPassword(id, locale, country).then { String uri ->
                        csrActionAudit(id)
                        if (debugEnabled || AuthorizeContext.debugEnabled) {
                            return Promise.pure(Response.ok().entity(uri).build())
                        }

                        Promise.pure(Response.ok().entity(Utils.maskEmail(userEmail)).build())
                    }
                }
            }
            else {
                throw AppCommonErrors.INSTANCE.fieldRequired('username or user_email').exception()
            }
        }

        Map<String, Object> requestScope = new HashMap<>()
        requestScope[ActionContextWrapper.PARAMETER_MAP] = formParams

        return flowExecutor.resume(conversationId, event ?: '', requestScope).then(ResponseUtil.WRITE_RESPONSE_CLOSURE)
    }

    @Override
    Promise<List<String>> getResetPasswordLink(String username, String userEmail, String locale, String country) {
        return userService.getResetPasswordLinks(username, userEmail, locale, country)
    }

    private void csrActionAudit(UserId userId) {
        if (AuthorizeContext.hasScopes('csr') && AuthorizeContext.currentUserId != null) {
            String email = userService.getUserEmailByUserId(userId).get()
            csrLogResource.create(new CsrLog(userId: AuthorizeContext.currentUserId, regarding: 'Account', action: com.junbo.csr.spec.def.CsrLogActionType.ResetPasswordEmailSent, property: email)).get()
        }
    }
}
