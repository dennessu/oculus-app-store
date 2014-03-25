/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.token.spec.model.*;
import com.junbo.token.spec.model.TokenOrderSearchParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * token resource interface.
 */
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface TokenResource {
    @POST
    @Path("/token-orders")
    Promise<OrderRequest> postOrder(OrderRequest request);

    @GET
    @Path("/token-orders/{tokenOrderId}")
    Promise<OrderRequest> getOrderById(@PathParam("tokenOrderId") Long tokenOrderId);

    @GET
    @Path("/token-orders/search")
    Promise<ResultList<OrderRequest>> searchOrder(
            @BeanParam TokenOrderSearchParam searchParam,
            @BeanParam PageMetaData pageMetadata);

    @POST
    @Path("/tokens/{tokenString}/consumption")
    Promise<TokenItem> consumeToken(@PathParam("tokenString") String tokenString);

    @PUT
    @Path("/tokens/{tokenString}")
    Promise<TokenItem> updateToken(@PathParam("tokenString") String tokenString,
                                   TokenItem token);

    @GET
    @Path("/tokens/{tokenString}")
    Promise<TokenItem> getToken(@PathParam("tokenString") String tokenString);
}
