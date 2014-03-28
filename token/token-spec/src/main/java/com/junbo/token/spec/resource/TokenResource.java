/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.resource;

import com.junbo.common.id.TokenOrderId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.token.spec.model.*;
import com.junbo.token.spec.model.TokenOrderSearchParam;

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
    @Path("/orders")
    Promise<OrderRequest> postOrder(OrderRequest request);

    @GET
    @Path("/orders/{tokenOrderId}")
    Promise<OrderRequest> getOrderById(@PathParam("tokenOrderId") TokenOrderId tokenOrderId);

    @GET
    @Path("/orders/search")
    Promise<ResultList<OrderRequest>> searchOrder(
            @BeanParam TokenOrderSearchParam searchParam,
            @BeanParam PageMetaData pageMetadata);

    @POST
    @Path("/{tokenString}/consumption")
    Promise<TokenItem> consumeToken(@PathParam("tokenString") String tokenString);

    @PUT
    @Path("/{tokenString}")
    Promise<TokenItem> updateToken(@PathParam("tokenString") String tokenString,
                                   TokenItem token);

    @GET
    @Path("/{tokenString}")
    Promise<TokenItem> getToken(@PathParam("tokenString") String tokenString);
}
