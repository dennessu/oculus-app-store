/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Javadoc.
 */
@Api("oauth2")
@Path("/oauth2/token")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public interface TokenEndpoint {

    @ApiOperation("Exchange for access token")
    @POST
    Promise<AccessTokenResponse> postToken(MultivaluedMap<String, String> formParams);

    @POST
    @Path("/explicit")
    Promise<AccessTokenResponse> postToken(@FormParam("client_id") String clientId,
                                           @FormParam("client_secret") String clientSecret,
                                           @FormParam("grant_type") String grantType,
                                           @FormParam("code") String code,
                                           @FormParam("scope") String scope,
                                           @FormParam("redirect_uri") String redirectUri,
                                           @FormParam("username") String username,
                                           @FormParam("password") String password,
                                           @FormParam("refresh_token") String refreshToken,
                                           @FormParam("nonce") String nonce);
}
