/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.builder;

import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.RatingResultEntry;
import com.junbo.rating.spec.model.request.OfferRatingItem;
import com.junbo.rating.spec.model.request.OfferRatingRequest;

import java.util.HashSet;

/**
 * Created by lizwu on 2/26/14.
 */
public class OfferRatingResultBuilder {

    private OfferRatingResultBuilder() {
    }

    public static OfferRatingRequest build(RatingContext context) {
        OfferRatingRequest result = new OfferRatingRequest();
        result.setUserId(context.getUserId());
        result.setCurrency(context.getCurrency().getCode());
        result.setTime(context.getTimestamp());
        result.setOffers(new HashSet<OfferRatingItem>());
        for (RatingResultEntry entry : context.getEntries()) {
            OfferRatingItem item = new OfferRatingItem();
            item.setOfferId(entry.getOfferId());
            item.setOriginalAmount(entry.getOriginalAmount().getValue());
            item.setDiscountAmount(entry.getDiscountAmount().getValue());
            item.setFinalAmount(item.getOriginalAmount().subtract(item.getDiscountAmount()));
            item.setPromotions(entry.getAppliedPromotion());
            result.getOffers().add(item);
        }

        return result;
    }
}
