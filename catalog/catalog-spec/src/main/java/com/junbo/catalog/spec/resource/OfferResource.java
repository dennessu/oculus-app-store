/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.common.id.OfferId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Offer resource definition.
 */
@Api("offers")
@Path("offers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface OfferResource {

    @ApiOperation("Get or search offers")
    @GET
    @Path("/")
    Promise<Results<Offer>> getOffers(@BeanParam OffersGetOptions options);

    @ApiOperation("Get an offer")
    @GET
    @Path("/{offerId}")
    Promise<Offer> getOffer(@PathParam("offerId") OfferId offerId, @BeanParam EntityGetOptions options);

    /**
     * Create a draft offer, the created offer is not purchasable until it is released.
     *
     * @param offer the offer to be created.
     * @return the created offer.
     */
    @ApiOperation("Create an offer")
    @POST
    @Path("/")
    Promise<Offer> create(Offer offer);

    @ApiOperation("Put an offer")
    @PUT
    @Path("/{offerId}")
    Promise<Offer> update(@PathParam("offerId") OfferId offerId, Offer offer);
}
