/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.rest.resource;

import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.spec.error.AppErrors;
import com.junbo.langur.core.promise.Promise;
import com.junbo.rating.core.builder.RatingResultBuilder;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.core.service.OfferRatingService;
import com.junbo.rating.core.service.OrderRatingService;
import com.junbo.rating.core.service.SubsRatingService;
import com.junbo.rating.spec.model.request.RatingRequest;
import com.junbo.rating.spec.model.subscription.SubsRatingRequest;
import com.junbo.rating.spec.resource.RatingResource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RatingResourceImpl.
 */
public class RatingResourceImpl implements RatingResource {
    private static final String RATING_SERVICE_SCOPE = "rating.service";

    @Autowired
    private OfferRatingService offerRatingService;

    @Autowired
    private OrderRatingService orderRatingService;

    @Autowired
    private SubsRatingService subsRatingService;

    @Override
    public Promise<RatingRequest> priceRating(RatingRequest request) {
        authorize();
        PriceRatingContext context = new PriceRatingContext();
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
    public Promise<SubsRatingRequest> subsRating(SubsRatingRequest request) {
        authorize();
        SubsRatingContext context = new SubsRatingContext();
        context.fromRequest(request);

        subsRatingService.rate(context);

        SubsRatingRequest response = RatingResultBuilder.buildForSubs(context);
        return Promise.pure(response);
    }

    private static void authorize() {
        if (!AuthorizeContext.hasScopes(RATING_SERVICE_SCOPE)) {
            throw AppErrors.INSTANCE.insufficientScope().exception();
        }
    }
}
