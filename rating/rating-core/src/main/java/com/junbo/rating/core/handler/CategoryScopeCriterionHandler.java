/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.criterion.CategoryScopeCriterion;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.spec.model.RatableItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by lizwu on 5/20/14.
 */
public class CategoryScopeCriterionHandler implements CriterionHandler<CategoryScopeCriterion> {
    @Override
    public boolean validate(CategoryScopeCriterion criterion, PriceRatingContext context) {
        RatableItem item = context.getCurrentItem();
        List<String> entities = criterion.getCategories();
        switch (criterion.getPredicate()) {
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
        }
        return false;
    }
}
