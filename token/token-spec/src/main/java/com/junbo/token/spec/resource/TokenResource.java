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
import javax.ws.rs.core.Response;

/**
 * payment instrument resource interface.
 */
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface TokenResource {
    @POST
    @Path("/token-orders/generation")
    Promise<TokenOrder> postOrderGeneration(TokenOrder request);

    @POST
    @Path("/token-orders/upload")
    Promise<TokenOrder> postOrderUpload(TokenOrder request);

    @GET
    @Path("/token-orders/{tokenOrderId}")
    Promise<TokenOrder> getById(@PathParam("tokenOrderId") Long tokenOrderId);

    @GET
    @Path("/token-orders/search")
    Promise<ResultList<TokenOrder>> searchPaymentInstrument(
            @BeanParam TokenOrderSearchParam searchParam,
            @BeanParam PageMetaData pageMetadata);

    @POST
    @Path("/tokens/{tokenString}/consumption")
    Promise<Response> delete(@PathParam("tokenString") String tokenString);

    @PUT
    @Path("/{tokenString}")
    Promise<TokenItem> update(@PathParam("tokenString") String tokenString);

    @GET
    @Path("/tokens/{tokenString}")
    Promise<TokenItem> getToken(@PathParam("tokenString") String tokenString);
}
