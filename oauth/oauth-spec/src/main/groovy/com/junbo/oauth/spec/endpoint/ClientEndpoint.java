/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.Client;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ClientEndpoint.
 */
@Api("oauth2")
@Path("/oauth2/clients")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface ClientEndpoint {

    @ApiOperation("Create a new client")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Client> postClient(@HeaderParam("Authorization") String authorization,
                               Client client);

    @ApiOperation("Get a client")
    @GET
    @Path("/{clientId}")
    Promise<Client> getByClientId(@HeaderParam("Authorization") String authorization,
                                  @PathParam("clientId") String clientId);

    @ApiOperation("Get client info")
    @GET
    @Path("/{clientId}/client-info")
    Promise<Client> getInfoByClientId(@HeaderParam("Authorization") String authorization,
                                      @PathParam("clientId") String clientId);

    @ApiOperation("Put a client")
    @PUT
    @Path("/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Client> putClient(@HeaderParam("Authorization") String authorization,
                              @PathParam("clientId") String clientId,
                              Client client);

    @ApiOperation(value = "Delete a client")
    @DELETE
    @Path("/{clientId}")
    Promise<Response> deleteClient(@HeaderParam("Authorization") String authorization,
                                   @PathParam("clientId") String clientId);

    @ApiOperation("Reset client secret")
    @POST
    @Path("/{clientId}/reset-secret")
    Promise<Client> resetSecret(@HeaderParam("Authorization") String authorization,
                                @PathParam("clientId") String clientId);
}
