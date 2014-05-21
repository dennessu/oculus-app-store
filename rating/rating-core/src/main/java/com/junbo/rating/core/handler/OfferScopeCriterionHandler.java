/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.criterion.OfferScopeCriterion;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.RatableItem;

import java.util.List;

/**
 * Created by lizwu on 2/21/14.
 */
public class OfferScopeCriterionHandler implements CriterionHandler<OfferScopeCriterion> {
    @Override
    public boolean validate(OfferScopeCriterion criterion, RatingContext context) {
        RatableItem item = context.getCurrentItem();
        List<Long> entities = criterion.getOffers();
        switch (criterion.getPredicate()) {
            case INCLUDE_OFFER:
                if (entities.contains(item.getOfferId())) {
                    return true;
                }
                break;
            case EXCLUDE_OFFER:
                if (!entities.contains(item.getOfferId())) {
                    return true;
                }
                break;
        }
        return false;
    }
}