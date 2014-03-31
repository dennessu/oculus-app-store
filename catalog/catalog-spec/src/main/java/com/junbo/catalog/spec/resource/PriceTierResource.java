/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.pricetier.PriceTiersGetOptions;
import com.junbo.common.id.PriceTierId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Price tier resource.
 */
@Api("price-tiers")
@Path("price-tiers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface PriceTierResource {

    @ApiOperation("Get a price tier")
    @GET
    @Path("/{tierId}")
    Promise<PriceTier> getPriceTier(@PathParam("tierId") PriceTierId tierId);

    @ApiOperation("Get all price tiers")
    @GET
    @Path("/")
    Promise<Results<PriceTier>> getPriceTiers(@BeanParam PriceTiersGetOptions options);

    @ApiOperation("Create a price tier")
    @POST
    @Path("/")
    Promise<PriceTier> createPriceTier(PriceTier attribute);
}
