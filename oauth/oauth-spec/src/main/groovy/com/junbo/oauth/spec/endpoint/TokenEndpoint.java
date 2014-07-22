/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.AccessTokenRequest;
import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Javadoc.
 */
@Api("oauth2")
@Path("/oauth2/token")
@RestResource
@InProcessCallable
@AuthorizationNotRequired
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public interface TokenEndpoint {

    @ApiOperation("Exchange for access token")
    @POST
    Promise<AccessTokenResponse> postToken(@BeanParam AccessTokenRequest request);
}

