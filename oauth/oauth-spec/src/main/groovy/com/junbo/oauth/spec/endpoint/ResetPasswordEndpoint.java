/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.AuthorizationNotRequired;
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
@AuthorizationNotRequired
@Produces(MediaType.APPLICATION_JSON)
public interface ResetPasswordEndpoint {
    //@GET
    //@Path("/forget-password")
    //Promise<Response> forgetPassword(@QueryParam("cid") String conversationId, @QueryParam("locale") String locale);

    //@POST
    //@Path("/forget-password")
    //Promise<Response> forgetPassword(@FormParam("cid") String conversationId,
    //                                 @FormParam("event") String event,
    //                                 MultivaluedMap<String, String> formParams);

    @GET
    @Path("/reset-password")
    Promise<Response> resetPasswordLink(@QueryParam("cid") String conversationId,
                                        @QueryParam("rpc") String code,
                                        @QueryParam("locale") String locale,
                                        @QueryParam("country") String country);
    @POST
    @Path("/reset-password")
    Promise<Response> resetPassword(@FormParam("cid") String conversationId,
                                    @FormParam("event") String event,
                                    @FormParam("locale") String locale,
                                    @FormParam("country") String country,
                                    @FormParam("username") String username,
                                    @FormParam("user_email") String userEmail,
                                    @Context ContainerRequestContext request,
                                    MultivaluedMap<String, String> formParams);
}
