/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.RestResource;
import com.junbo.billing.spec.model.Currency;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by xmchen on 14-2-13.
 */
@Path("/currencies")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CurrencyResource {
    @GET
    Promise<List<Currency>> getCurrencies();

    @GET
    @Path("/{name}")
    Promise<Currency> getCurrency(@PathParam("name") String name);

}
