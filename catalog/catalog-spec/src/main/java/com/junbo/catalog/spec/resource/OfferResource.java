/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Offer resource definition.
 */
@Path("offers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface OfferResource {
    @GET
    @Path("/")
    Promise<ResultList<Offer>> getOffers(@BeanParam EntitiesGetOptions options);

    @GET
    @Path("/{offerId}")
    Promise<Offer> getOffer(@PathParam("offerId") Long offerId, @BeanParam EntityGetOptions options);

    /**
     * Create a draft offer, the created offer is not purchasable until it is released.
     *
     * @param offer the offer to be created.
     * @return the created offer.
     */
    @POST
    @Path("/")
    Promise<Offer> create(Offer offer);

    @PUT
    @Path("/{offerId}")
    Promise<Offer> update(@PathParam("offerId") Long offerId, Offer offer);
}
