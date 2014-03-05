/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.builder;

import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.RatingResultEntry;
import com.junbo.rating.spec.model.request.OrderBenefit;
import com.junbo.rating.spec.model.request.OrderRatingItem;
import com.junbo.rating.spec.model.request.OrderRatingRequest;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Created by lizwu on 2/26/14.
 */
public class OrderRatingResultBuilder {

    private OrderRatingResultBuilder() {
    }

    public static OrderRatingRequest build(RatingContext context) {
        OrderRatingRequest result = new OrderRatingRequest();
        result.setUserId(context.getUserId());
        result.setCountry(context.getCountry());
        result.setCurrency(context.getCurrency());
        result.setLineItems(new HashSet<OrderRatingItem>());
        for (RatingResultEntry entry : context.getEntries()) {
            OrderRatingItem item = new OrderRatingItem();
            item.setOfferId(entry.getOfferId());
            item.setQuantity(entry.getQuantity());
            item.setOriginalAmount(entry.getOriginalAmount().getValue());
            item.setDiscountAmount(entry.getDiscountAmount().getValue());
            item.setFinalAmount(item.getOriginalAmount().subtract(item.getDiscountAmount()));
            item.setPromotions(new HashSet<Long>());
            item.getPromotions().addAll(entry.getAppliedPromotion());
            result.getLineItems().add(item);
        }

        OrderBenefit orderBenefit = new OrderBenefit();
        orderBenefit.setDiscountAmount(context.getOrderResultEntry().getDiscountAmount().getValue());
        BigDecimal finalTotalAmount = context.getOrderResultEntry().getOriginalAmount().subtract(
                context.getOrderResultEntry().getDiscountAmount()).getValue();
        orderBenefit.setFinalAmount(finalTotalAmount);
        orderBenefit.setPromotion(context.getOrderResultEntry().getAppliedPromotion());
        result.setOrderBenefit(orderBenefit);
        return result;
    }
}
