/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.token.spec.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * token resource interface.
 */
@Path("/tokens")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface TokenResource {
    @POST
    @Path("/requests")
    Promise<TokenRequest> postOrder(TokenRequest request);

    @GET
    @Path("/requests/{tokenRequestId}")
    Promise<TokenRequest> getOrderById(@PathParam("tokenRequestId") String tokenOrderId);

    @POST
    @Path("/consumption")
    Promise<TokenConsumption> consumeToken(TokenConsumption consumption);

    @PUT
    @Path("/{tokenString}")
    Promise<TokenItem> updateToken(@PathParam("tokenString") String tokenString,
                                   TokenItem token);

    @GET
    @Path("/{tokenString}")
    Promise<TokenItem> getToken(@PathParam("tokenString") String tokenString);
}
