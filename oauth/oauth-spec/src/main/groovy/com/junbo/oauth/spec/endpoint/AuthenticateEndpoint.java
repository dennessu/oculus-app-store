/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;

/**
 * Javadoc.
 */
@Path("authenticate")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
@Consumes("application/x-www-form-urlencoded")
public interface AuthenticateEndpoint {

    @POST
    Promise<Response> postAuthenticate(@Context HttpHeaders httpHeaders,
                                       MultivaluedMap<String, String> formParams,
                                       @Context ContainerRequestContext request);
}
