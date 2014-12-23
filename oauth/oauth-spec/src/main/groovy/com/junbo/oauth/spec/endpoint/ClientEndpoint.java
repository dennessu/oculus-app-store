/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.common.model.Results;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.Client;
import com.junbo.oauth.spec.option.PageableGetOptions;
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
@InProcessCallable
@Produces(MediaType.APPLICATION_JSON)
public interface ClientEndpoint {

    @ApiOperation("View all clients")
    @GET
    Promise<Results<Client>> getClients(@BeanParam PageableGetOptions options);

    @ApiOperation("Create a new client")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Client> postClient(Client client);

    @ApiOperation("Get a client")
    @GET
    @Path("/{clientId}")
    Promise<Client> getByClientId(@PathParam("clientId") String clientId);

    @ApiOperation("Get client info")
    @GET
    @Path("/{clientId}/client-info")
    Promise<Client> getInfoByClientId(@PathParam("clientId") String clientId);

    @ApiOperation("Put a client")
    @PUT
    @Path("/{clientId}")
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Client> putClient(@PathParam("clientId") String clientId, Client client);

    @ApiOperation(value = "Delete a client")
    @DELETE
    @Path("/{clientId}")
    Promise<Response> deleteClient(@PathParam("clientId") String clientId);

    @ApiOperation("Reset client secret")
    @POST
    @Path("/{clientId}/reset-secret")
    Promise<Client> resetSecret(@PathParam("clientId") String clientId);
}
