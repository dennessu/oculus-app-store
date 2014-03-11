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
import com.junbo.common.id.Id;
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
    Promise<Promotion> getPromotion(@PathParam("promotionId") Id promotionId, @BeanParam EntityGetOptions options);

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
    Promise<Promotion> update(@PathParam("promotionId") Id promotionId, @Valid Promotion promotion);
}
