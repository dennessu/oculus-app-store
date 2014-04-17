/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.rating.core.builder.OfferRatingResultBuilder;
import com.junbo.rating.core.builder.RatingResultBuilder;
import com.junbo.rating.core.service.OfferRatingService;
import com.junbo.rating.core.service.OrderRatingService;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.request.OfferRatingRequest;
import com.junbo.rating.spec.model.request.RatingRequest;
import com.junbo.rating.spec.resource.RatingResource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RatingResourceImpl.
 */
public class RatingResourceImpl implements RatingResource{
    @Autowired
    private OfferRatingService offerRatingService;

    @Autowired
    private OrderRatingService orderRatingService;

    @Override
    public Promise<RatingRequest> priceRating(RatingRequest request) {
        RatingContext context = new RatingContext();
        context.fromRequest(request);

        if(request.getIncludeCrossOfferPromos()) {
            orderRatingService.rate(context);
        } else {
            offerRatingService.rate(context);
        }

        RatingRequest response = request.getIncludeCrossOfferPromos()?
                RatingResultBuilder.buildForOrder(context) :  RatingResultBuilder.buildForOffers(context);
        return Promise.pure(response);
    }

    @Override
    public Promise<OfferRatingRequest> offerRating(OfferRatingRequest request) {
        RatingContext context = new RatingContext();
        context.fromRequest(request);

        offerRatingService.offerRating(context);

        OfferRatingRequest response = OfferRatingResultBuilder.build(context);
        return Promise.pure(response);
    }
}
