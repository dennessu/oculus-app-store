/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * EmailVerifyEndpoint.
 */
@Api("oauth2")
@Path("/oauth2/verify-email")
@RestResource
@InProcessCallable
@Produces(MediaType.APPLICATION_JSON)
public interface EmailVerifyEndpoint {
    @GET
    @AuthorizationNotRequired
    Promise<Response> verifyEmail(@QueryParam("evc") String evc,
                                  @QueryParam("locale") String locale);

    @POST
    Promise<Response> sendVerifyEmail(@FormParam("locale") String locale,
                                      @FormParam("country") String country,
                                      @FormParam("userId") UserId userId,
                                      @FormParam("tml") UserPersonalInfoId targetMail);

    @GET
    @Path("/test")
    Promise<List<String>> getVerifyEmailLink(@QueryParam("userId") UserId userId,
                                       @QueryParam("locale") String locale,
                                       @QueryParam("email") String email);
}
