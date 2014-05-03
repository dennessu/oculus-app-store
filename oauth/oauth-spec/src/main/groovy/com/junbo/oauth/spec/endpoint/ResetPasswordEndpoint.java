/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.common.id.UserId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Created by minhao on 5/1/14.
 */
@Api("oauth2")
@Path("/oauth2")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface ResetPasswordEndpoint {

    @GET
    @Path("/reset-password")
    Promise<Response> resetPasswordStart(@QueryParam("rpc") String code,
                                    @QueryParam("locale") String locale,
                                    @QueryParam("cid") String cid);
    @POST
    @Path("/reset-password")
    Promise<Response> resetPassword(@FormParam("cid") String conversationId,
                                    @FormParam("event") String event,
                                    MultivaluedMap<String, String> formParams);

    @POST
    @Path("/reset-password-pong")
    Promise<Response> sendResetPasswordEmail(@HeaderParam("Authorization") String authorization,
                                      @FormParam("locale") String locale,
                                      @FormParam("userId") UserId userId,
                                      @Context ContainerRequestContext request);
}
