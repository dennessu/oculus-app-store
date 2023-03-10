/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.core.service.OfferRatingService;
import com.junbo.rating.core.service.OrderRatingService;
import com.junbo.rating.core.service.SubsRatingService;
import com.junbo.rating.spec.model.priceRating.RatingRequest;
import com.junbo.rating.spec.model.subscription.SubsRatingRequest;
import com.junbo.rating.spec.resource.RatingResource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RatingResourceImpl.
 */
public class RatingResourceImpl implements RatingResource {
    @Autowired
    private OfferRatingService offerRatingService;

    @Autowired
    private OrderRatingService orderRatingService;

    @Autowired
    private SubsRatingService subsRatingService;

    @Override
    public Promise<RatingRequest> priceRating(RatingRequest request) {
        PriceRatingContext context = new PriceRatingContext();
        context.fromRequest(request);

        if(request.getIncludeCrossOfferPromos()) {
            orderRatingService.rate(context);
        } else {
            offerRatingService.rate(context);
        }

        RatingRequest response = context.buildResult();
        return Promise.pure(response);
    }

    @Override
    public Promise<SubsRatingRequest> subsRating(SubsRatingRequest request) {
        SubsRatingContext context = new SubsRatingContext();
        context.fromRequest(request);

        subsRatingService.rate(context);

        SubsRatingRequest response = context.buildResult();
        return Promise.pure(response);
    }
}
