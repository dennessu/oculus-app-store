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

import javax.validation.Valid;
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
    Promise<Offer> create(@Valid Offer offer);

    @PUT
    @Path("/{offerId}")
    Promise<Offer> update(@Valid Offer offer);

    /**
     * Developer submit an draft offer for review.
     * @param offerId the id of the offer to be reviewed.
     * @return the offer to be reviewed.
     */
    @POST
    @Path("/{offerId}/review")
    Promise<Offer> review(@PathParam("offerId") Long offerId);

    /**
     * Admin publishes an offer, makes it purchasable.
     * @param offerId the id of offer to be released.
     * @return the offer to be released.
     */
    @POST
    @Path("/{offerId}/release")
    Promise<Offer> release(@PathParam("offerId") Long offerId);

    /**
     * Admin rejects an offer, developer may update and submit review later.
     * @param offerId the id of offer to be released.
     * @return the offer to be released.
     */
    // TODO: add review notes
    @POST
    @Path("/{offerId}/reject")
    Promise<Offer> reject(@PathParam("offerId") Long offerId);

    /**
     * Remove an offer, makes it not purchasable. The draft version is still kept.
     * Developer may update and submit review again in future.
     * @param offerId the id of offer to be removed.
     * @return the removed offer id.
     */
    @DELETE
    @Path("/{offerId}/release")
    Promise<Long> remove(@PathParam("offerId") Long offerId);

    /**
     * Delete an offer, delete both draft and released version.
     * Developer cannot operate this offer again in future.
     * @param offerId the id of offer to be deleted.
     * @return the deleted offer id.
     */
    @DELETE
    @Path("/{offerId}")
    Promise<Long> delete(@PathParam("offerId") Long offerId);
}
