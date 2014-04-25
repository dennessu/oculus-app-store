/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * EmailVerifyEndpoint.
 */
@Api("oauth2")
@Path("/oauth2/verify-email")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface EmailVerifyEndpoint {
    @GET
    Promise<Response> verifyEmail(@QueryParam("code") String code, @QueryParam("cid") String conversationId,
                                  @QueryParam("event") String event);
}
