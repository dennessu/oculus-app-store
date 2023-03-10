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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

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
    @AuthorizationNotRequired
    Promise<Response> resetPasswordLink(@QueryParam("cid") String conversationId,
                                        @QueryParam("rpc") String code,
                                        @QueryParam("locale") String locale,
                                        @QueryParam("country") String country);
    @POST
    @Path("/reset-password")
    @AuthorizationNotRequired
    Promise<Response> resetPassword(@FormParam("cid") String conversationId,
                                    @FormParam("event") String event,
                                    @FormParam("locale") String locale,
                                    @FormParam("country") String country,
                                    @FormParam("user_email") String userEmail,
                                    MultivaluedMap<String, String> formParams);

    @GET
    @Path("/reset-password/test")
    Promise<List<String>> getResetPasswordLink(@QueryParam("username") String username,
                                               @QueryParam("user_email") String userEmail,
                                               @QueryParam("locale") String locale,
                                               @QueryParam("country") String country);
}
