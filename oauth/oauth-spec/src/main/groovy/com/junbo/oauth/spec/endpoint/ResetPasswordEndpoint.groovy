package com.junbo.oauth.spec.endpoint

import com.junbo.common.id.UserId
import com.junbo.langur.core.RestResource
import com.junbo.langur.core.promise.Promise
import com.wordnik.swagger.annotations.Api

import javax.ws.rs.*
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

/**
 * Created by minhao on 5/1/14.
 */
@Api("oauth2")
@Path("/oauth2/reset-password")
@RestResource
public interface ResetPasswordEndpoint {
    @GET
    Promise<Response> resetPassword(@QueryParam("code") String code, @QueryParam("locale") String locale,
                                  @QueryParam("cid") String conversationId, @QueryParam("event") String event);

    @POST
    Promise<Response> sendResetPasswordEmail(@HeaderParam("Authorization") String authorization,
                                      @FormParam("locale") String locale,
                                      @FormParam("userId") UserId userId,
                                      @Context ContainerRequestContext request);
}