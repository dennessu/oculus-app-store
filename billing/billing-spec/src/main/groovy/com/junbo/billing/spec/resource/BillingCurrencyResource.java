/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.resource;

import com.junbo.billing.spec.model.Currency;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by xmchen on 14-2-13.
 */
@Path("/billing-currencies")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface BillingCurrencyResource {
    @GET
    Promise<Results<Currency>> getCurrencies();

    @GET
    @Path("/{name}")
    Promise<Currency> getCurrency(@PathParam("name") String name);

}
