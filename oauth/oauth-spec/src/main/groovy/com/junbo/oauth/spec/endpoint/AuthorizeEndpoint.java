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

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;

/**
 * Javadoc.
 */
@Api("oauth2")
@Path("/oauth2/authorize")
@RestResource
@AuthorizationNotRequired
@Produces(MediaType.APPLICATION_JSON)
public interface AuthorizeEndpoint {

    @ApiOperation(
            value = "Authorize the user by GET for simple redirect",
            notes = "The response is through redirect. For more details refer to the document.")
    @GET
    Promise<Response> authorize(@Context UriInfo uriInfo,
                                @Context HttpHeaders httpHeaders,
                                @Context ContainerRequestContext request);

    @ApiOperation(
            value = "Authorize the user",
            notes = "The response is through redirect. For more details refer to the document.")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Promise<Response> postAuthorize(@Context HttpHeaders httpHeaders,
                                    MultivaluedMap<String, String> formParams,
                                    @Context ContainerRequestContext request);
}
