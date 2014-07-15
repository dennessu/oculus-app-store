/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.pricetier.PriceTiersGetOptions;
import com.junbo.common.filter.annotations.CacheMaxAge;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

/**
 * Price tier resource.
 */
@Api("price-tiers")
@Path("price-tiers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource(sync = true)
public interface PriceTierResource {
    @CacheMaxAge(duration = 1, unit = TimeUnit.HOURS)
    @ApiOperation("Get a price tier")
    @GET
    @Path("/{tierId}")
    Promise<PriceTier> getPriceTier(@PathParam("tierId") String tierId);

    @CacheMaxAge(duration = 1, unit = TimeUnit.HOURS)
    @ApiOperation("Get all price tiers")
    @GET
    @Path("/")
    Promise<Results<PriceTier>> getPriceTiers(@BeanParam PriceTiersGetOptions options);

    @ApiOperation("Create a price tier")
    @POST
    @Path("/")
    Promise<PriceTier> createPriceTier(PriceTier attribute);

    @ApiOperation("Put an price tier")
    @PUT
    @Path("/{tierId}")
    Promise<PriceTier> update(@PathParam("tierId") String tierId, PriceTier priceTier);

    @ApiOperation("Delete an price tier")
    @DELETE
    @Path("/{tierId}")
    Promise<Response> delete(@PathParam("tierId") String tierId);
}
