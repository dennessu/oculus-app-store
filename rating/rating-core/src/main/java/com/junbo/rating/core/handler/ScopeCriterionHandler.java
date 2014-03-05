/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.ScopeCriterion;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.RatableItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by lizwu on 2/21/14.
 */
public class ScopeCriterionHandler implements CriterionHandler<ScopeCriterion> {
    @Override
    public boolean validate(ScopeCriterion criterion, RatingContext context) {
        RatableItem item = context.getCurrentItem();
        List<Long> entities = criterion.getEntities();
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
            case INCLUDE_CATEGORY:
                if (!Collections.disjoint(entities, item.getOffer().getCategories())) {
                    return true;
                }
                break;
            case EXCLUDE_CATEGORY:
                if (Collections.disjoint(entities, item.getOffer().getCategories())) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }
}
