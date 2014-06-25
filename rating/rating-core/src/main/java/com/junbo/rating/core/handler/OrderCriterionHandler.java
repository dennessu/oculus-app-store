/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.criterion.OrderCriterion;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.spec.model.RatingResultEntry;

import java.math.BigDecimal;

/**
 * Created by lizwu on 2/21/14.
 */
public class OrderCriterionHandler implements CriterionHandler<OrderCriterion> {
    @Override
    public boolean validate(OrderCriterion criterion, PriceRatingContext context) {
        switch (criterion.getPredicate()) {
            case ORDER_ABSOLUTE_VALUE_ABOVE:
                BigDecimal totalAmount = context.getOrderResult().getOriginalAmount();
                if (criterion.getThresholdValue().compareTo(totalAmount) <= 0) {
                    return true;
                }
                break;
            case ORDER_ITEM_COUNT_ABOVE:
                BigDecimal quantity = new BigDecimal(getQuantity(context));
                if (criterion.getThresholdValue().compareTo(quantity) <= 0) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    private int getQuantity(PriceRatingContext context) {
        int quantity = 0;
        for (RatingResultEntry entry : context.getEntries()) {
            quantity += entry.getQuantity();
        }
        return quantity;
    }
}
