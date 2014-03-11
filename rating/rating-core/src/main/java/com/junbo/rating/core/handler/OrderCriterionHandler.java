/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.OrderCriterion;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.RatingResultEntry;

import java.math.BigDecimal;

/**
 * Created by lizwu on 2/21/14.
 */
public class OrderCriterionHandler implements CriterionHandler<OrderCriterion> {
    @Override
    public boolean validate(OrderCriterion criterion, RatingContext context) {
        switch (criterion.getPredicate()) {
            case ORDER_ABSOLUTE_VALUE_ABOVE:
                BigDecimal totalAmount = context.getOrderResult().getOriginalAmount().getValue();
                if (criterion.getThresholdValue().compareTo(totalAmount) < 0) {
                    return true;
                }
                break;
            case ORDER_ITEM_COUNT_ABOVE:
                BigDecimal quantity = new BigDecimal(getQuantity(context));
                if (criterion.getThresholdValue().compareTo(quantity) < 0) {
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    private int getQuantity(RatingContext context) {
        int quantity = 0;
        for (RatingResultEntry entry : context.getEntries()) {
            quantity += entry.getQuantity();
        }
        return quantity;
    }
}
