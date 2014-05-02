package com.junbo.oauth.api.endpoint

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.ResetPasswordEndpoint
import groovy.transform.CompileStatic

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ResetPasswordEndpointImpl implements ResetPasswordEndpoint {
    @Override
    Promise<Response> resetPassword(String code, String locale, String conversationId, String event) {
        return null
    }

    @Override
    Promise<Response> sendResetPasswordEmail(String authorization, String locale, UserId userId, ContainerRequestContext request) {
        return null
    }
}
