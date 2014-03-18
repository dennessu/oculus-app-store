/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;

/**
 * Javadoc.
 */
@Path("authorize")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface AuthorizeEndpoint {

    @GET
    Promise<Response> authorize(@Context UriInfo uriInfo,
                                @Context HttpHeaders httpHeaders,
                                @Context ContainerRequestContext request);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Promise<Response> postAuthorize(@Context HttpHeaders httpHeaders,
                                    MultivaluedMap<String, String> formParams,
                                    @Context ContainerRequestContext request);
}
