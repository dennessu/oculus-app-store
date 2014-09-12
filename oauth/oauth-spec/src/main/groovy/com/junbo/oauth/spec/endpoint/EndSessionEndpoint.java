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
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;

/**
 * EndSessionEndpoint.
 */
@Api("oauth2")
@Path("/oauth2/end-session")
@RestResource
@AuthorizationNotRequired
@Produces(MediaType.APPLICATION_JSON)
public interface EndSessionEndpoint {

    @ApiOperation("Logout the user from web flow")
    @GET
    Promise<Response> endSession(@Context UriInfo uriInfo,
                                 @Context HttpHeaders httpHeaders,
                                 @Context ContainerRequestContext request);
}
