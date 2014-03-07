/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Promotion resource definition.
 */
@Path("promotions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface PromotionResource {
    @GET
    @Path("/")
    Promise<ResultList<Promotion>> getPromotions(@BeanParam EntitiesGetOptions options);

    @GET
    @Path("/{promotionId}")
    Promise<Promotion> getPromotion(@PathParam("promotionId") Long promotionId, @BeanParam EntityGetOptions options);

    /**
     * Create a draft promotion, the created promotion is not purchasable until it is released.
     *
     * @param promotion the promotion to be created.
     * @return the created promotion.
     */
    @POST
    @Path("/")
    Promise<Promotion> create(@Valid Promotion promotion);

    @PUT
    @Path("/{promotionId}")
    Promise<Promotion> update(@Valid Promotion promotion);

    /**
     * Developer submit an draft promotion for review.
     * @param promotionId the id of the promotion to be reviewed.
     * @return the promotion to be reviewed.
     */
    @POST
    @Path("/{promotionId}/review")
    Promise<Promotion> review(@PathParam("promotionId") Long promotionId);

    /**
     * Admin publishes an promotion, makes it purchasable.
     * @param promotionId the id of promotion to be released.
     * @return the promotion to be released.
     */
    @POST
    @Path("/{promotionId}/release")
    Promise<Promotion> release(@PathParam("promotionId") Long promotionId);

    /**
     * Admin rejects an promotion, developer may update and submit review later.
     * @param promotionId the id of promotion to be released.
     * @return the promotion to be released.
     */
    // TODO: add review notes
    @POST
    @Path("/{promotionId}/reject")
    Promise<Promotion> reject(@PathParam("promotionId") Long promotionId);

    /**
     * Remove a released promotion. The draft version is still kept.
     * Developer may update and submit review again in future.
     * @param promotionId the id of promotion to be removed.
     * @return the removed promotion id.
     */
    @DELETE
    @Path("/{promotionId}/release")
    Promise<Long> remove(@PathParam("promotionId") Long promotionId);

    /**
     * Delete an promotion, delete both draft and released version.
     * Developer cannot operate this promotion again in future.
     * @param promotionId the id of promotion to be deleted.
     * @return the deleted promotion id.
     */
    @DELETE
    @Path("/{promotionId}")
    Promise<Long> delete(@PathParam("promotionId") Long promotionId);
}
