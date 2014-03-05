/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.rating.core.service.OfferRatingService;
import com.junbo.rating.core.service.OrderRatingService;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.request.OfferRatingRequest;
import com.junbo.rating.spec.model.request.OrderRatingRequest;
import com.junbo.rating.spec.resource.RatingResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by lizwu on 2/18/14.
 */
@Path("rating")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class RatingResourceImpl implements RatingResource{
    @Autowired
    private OfferRatingService offerRatingService;

    @Autowired
    private OrderRatingService orderRatingService;

    @Override
    public Promise<OfferRatingRequest> offersRating(OfferRatingRequest request) {
        RatingContext context = new RatingContext();
        context.fromRequest(request);
        OfferRatingRequest response = offerRatingService.offerRating(context);
        return Promise.pure(response);
    }

    @Override
    public Promise<OrderRatingRequest> orderRating(OrderRatingRequest request) {
        RatingContext context = new RatingContext();
        context.fromRequest(request);
        OrderRatingRequest response = orderRatingService.orderRating(context);
        return Promise.pure(response);
    }
}
