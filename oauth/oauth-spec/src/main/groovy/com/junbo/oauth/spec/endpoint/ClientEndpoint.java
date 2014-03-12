/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.Client;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * ClientEndpoint.
 */
@Path("clients")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface ClientEndpoint {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Client> postClient(@HeaderParam("Authorization") String authorization,
                               Client client);

    @GET
    @Path("/{clientId}")
    Promise<Client> getByClientId(@HeaderParam("Authorization") String authorization,
                                  @PathParam("clientId") String clientId);

    @PUT
    @Path("/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Client> putClient(@HeaderParam("Authorization") String authorization,
                              @PathParam("clientId") String clientId,
                              Client client);

    @POST
    @Path("/{clientId}")
    Promise<Client> resetSecret(@HeaderParam("Authorization") String authorization,
                                @PathParam("clientId") String clientId);
}
