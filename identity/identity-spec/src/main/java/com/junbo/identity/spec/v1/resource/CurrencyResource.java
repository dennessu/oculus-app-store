/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions;
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by xiali_000 on 4/21/2014.
 */
@Api(value = "currencies")
@RestResource
@Path("/currencies")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface CurrencyResource {

    @ApiOperation("Create one Currency info")
    @POST
    Promise<Currency> create(Currency currency);

    @ApiOperation("Update one currency info")
    @PUT
    @Path("/{currencyId}")
    Promise<Currency> put(@PathParam("currencyId") CurrencyId currencyId, Currency currency);

    @POST
    @Path("/{currencyId}")
    Promise<Currency> patch(@PathParam("currencyId") CurrencyId currencyId, Currency currency);

    @ApiOperation("Get one currency info")
    @GET
    @Path("/{currencyId}")
    Promise<Currency> get(@PathParam("currencyId") CurrencyId currencyId, @BeanParam CurrencyGetOptions getOptions);

    @ApiOperation("Search currency info")
    @GET
    Promise<Results<Currency>> list(@BeanParam CurrencyListOptions listOptions);

    // Delete operation won't be supported.
}
