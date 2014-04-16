/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import com.junbo.common.id.PromotionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Promotion resource definition.
 */
@Api("promotions")
@Path("promotions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface PromotionResource {

    @ApiOperation("Get or search promotions")
    @GET
    @Path("/")
    Promise<Results<Promotion>> getPromotions(@BeanParam PromotionsGetOptions options);

    @ApiOperation("Get a promotion")
    @GET
    @Path("/{promotionId}")
    Promise<Promotion> getPromotion(@PathParam("promotionId") PromotionId promotionId);

    @ApiOperation("Create a promotion")
    @POST
    @Path("/")
    Promise<Promotion> create(Promotion promotion);

    @ApiOperation("Put a promotion")
    @PUT
    @Path("/{promotionId}")
    Promise<Promotion> update(@PathParam("promotionId") PromotionId promotionId, Promotion promotion);


    @ApiOperation("Delete a promotion")
    @DELETE
    @Path("/{promotionId}")
    Promise<Response> delete(@PathParam("promotionId") PromotionId promotionId);
}
