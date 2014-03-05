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
    @Path("/{promotionId}")
    Promise<Promotion> getPromotion(@PathParam("promotionId") Long promotionId,
                                    @BeanParam EntityGetOptions options);

    @GET
    Promise<ResultList<Promotion>> getPromotions(@BeanParam EntitiesGetOptions options);

    @POST
    Promise<Promotion> createPromotion(@Valid Promotion promotion);
}
