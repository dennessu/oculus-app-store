/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.resource;


import com.junbo.billing.spec.model.Balance;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.OrderId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by xmchen on 14-1-26.
 */
@Path("/balances")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@InProcessCallable
public interface BalanceResource {
    @POST
    Promise<Balance> postBalance(Balance balance);

    @POST
    @Path("/quote")
    Promise<Balance> quoteBalance(Balance balance);

    @POST
    @Path("/capture")
    Promise<Balance> captureBalance(Balance balance);

    @POST
    @Path("/confirm")
    Promise<Balance> confirmBalance(Balance balance);

    @POST
    @Path("/check")
    Promise<Balance> checkBalance(Balance balance);

    @POST
    @Path("/process-async")
    Promise<Balance> processAsyncBalance(Balance balance);

    @GET
    @Path("/{balanceId}")
    Promise<Balance> getBalance(@PathParam("balanceId") BalanceId balanceId);

    @GET
    Promise<Results<Balance>> getBalances(@QueryParam("orderId") OrderId orderId);

    @PUT
    Promise<Balance> putBalance(Balance balance);

    @POST
    @Path("/audit")
    Promise<Balance> auditBalance(Balance balance);
}
