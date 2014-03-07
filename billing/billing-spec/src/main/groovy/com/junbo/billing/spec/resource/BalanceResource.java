/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.resource;


import com.junbo.billing.spec.model.Balance;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.OrderId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
@Path("/balances")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface BalanceResource {
    @POST
    Promise<Balance> postBalance(Balance balance);

    @POST
    @Path("/quote")
    Promise<Balance> quoteBalance(Balance balance);

    @GET
    @Path("/{balanceId}")
    Promise<Balance> getBalance(@PathParam("balanceId") BalanceId balanceId);

    @GET
    Promise<List<Balance>> getBalances(@QueryParam("orderId") OrderId orderId);
}
