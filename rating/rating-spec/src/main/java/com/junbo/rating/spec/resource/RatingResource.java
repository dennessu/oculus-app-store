/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.resource;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.rating.spec.model.request.RatingRequest;
import com.junbo.rating.spec.model.request.OfferRatingRequest;
import com.junbo.rating.spec.model.subscription.SubsRatingRequest;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Created by lizwu on 1/28/14.
 */
@Api("price-rating")
@Path("/price-rating")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface RatingResource {
    @ApiOperation("Rate price for offers.")
    @POST
    @Path("/")
    Promise<RatingRequest> priceRating(@Valid RatingRequest request);

    @POST
    @Path("/offers")
    Promise<OfferRatingRequest> offerRating(@Valid OfferRatingRequest request);

    @POST
    @Path("/subsOffer")
    Promise<SubsRatingRequest> subsRating(@Valid SubsRatingRequest request);
}
