/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * RevokeEndpoint.
 */
@Api(value = "oauth2", basePath = "oauth2")
@Path("/oauth2/revoke")
@RestResource
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public interface RevokeEndpoint {

    @ApiOperation("Revoke an access token")
    @POST
    Promise<Response> revoke(@HeaderParam("Authorization") String authorization, @FormParam("token") String token,
                             @FormParam("token_type_hint") String tokenTypeHint);

    @ApiOperation("Revoke the consent to a client")
    @POST
    @Path("/consent")
    Promise<Response> revokeConsent(@HeaderParam("Authorization") String authorization,
                                    @FormParam("client_id") String clientId);
}
