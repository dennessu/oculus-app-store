/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
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
 * Offer resource definition.
 */
@Api("offers")
@Path("offers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource(sync = true)
public interface OfferResource {
    @CacheMaxAge(duration = 5, unit = TimeUnit.MINUTES)
    @ApiOperation("Get or search offers")
    @GET
    @Path("/")
    Promise<Results<Offer>> getOffers(@BeanParam OffersGetOptions options);

    @CacheMaxAge(duration = 5, unit = TimeUnit.MINUTES)
    @ApiOperation("Get an offer")
    @GET
    @Path("/{offerId}")
    Promise<Offer> getOffer(@PathParam("offerId") String offerId);

    @ApiOperation("Create an offer")
    @POST
    @Path("/")
    Promise<Offer> create(Offer offer);

    @ApiOperation("Put an offer")
    @PUT
    @Path("/{offerId}")
    Promise<Offer> update(@PathParam("offerId") String offerId, Offer offer);

    @ApiOperation("Delete an offer")
    @DELETE
    @Path("/{offerId}")
    Promise<Response> delete(@PathParam("offerId") String offerId);
}
