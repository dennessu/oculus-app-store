/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.builder;

import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.RatingResultEntry;
import com.junbo.rating.spec.model.request.*;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Created by lizwu on 2/26/14.
 */
public class RatingResultBuilder {

    public static RatingRequest buildForOrder(RatingContext context) {
        RatingRequest result = new RatingRequest();
        result.setUserId(context.getUserId());
        result.setCurrency(context.getCurrency().getCode());
        result.setLineItems(new HashSet<RatingItem>());
        result.setShippingMethodId(context.getDefaultShippingMethod());

        //build offer level results
        for (RatingResultEntry entry : context.getEntries()) {
            RatingItem item = new RatingItem();
            item.setOfferId(entry.getOfferId());
            item.setQuantity(entry.getQuantity());
            item.setShippingMethodId(entry.getShippingMethodId());
            item.setOriginalUnitPrice(entry.getOriginalAmount().getValue());
            item.setOriginalTotalPrice(entry.getOriginalAmount().multiple(entry.getQuantity()).getValue());
            item.setTotalDiscountAmount(entry.getDiscountAmount().multiple(entry.getQuantity()).getValue());
            item.setFinalTotalAmount(item.getOriginalTotalPrice().subtract(item.getTotalDiscountAmount()));
            item.setPromotions(entry.getAppliedPromotion());
            result.getLineItems().add(item);
        }

        //build order level results
        OrderBenefit orderBenefit = new OrderBenefit();
        orderBenefit.setDiscountAmount(context.getOrderResult().getDiscountAmount().getValue());
        BigDecimal finalTotalAmount = context.getOrderResult().getOriginalAmount().subtract(
                context.getOrderResult().getDiscountAmount()).getValue();
        orderBenefit.setFinalAmount(finalTotalAmount);
        orderBenefit.setPromotion(context.getOrderResult().getAppliedPromotion());
        result.setOrderBenefit(orderBenefit);

        //build shipping fee calculation results
        ShippingBenefit shippingBenefit = new ShippingBenefit();
        shippingBenefit.setShippingFee(context.getShippingResult().getShippingFee());
        shippingBenefit.setPromotion(context.getShippingResult().getAppliedPromotion());
        result.setShippingBenefit(shippingBenefit);

        return result;
    }

    public static RatingRequest buildForOffers(RatingContext context) {
        RatingRequest result = new RatingRequest();
        result.setUserId(context.getUserId());
        result.setCurrency(context.getCurrency().getCode());
        result.setLineItems(new HashSet<RatingItem>());
        for (RatingResultEntry entry : context.getEntries()) {
            RatingItem item = new RatingItem();
            item.setOfferId(entry.getOfferId());
            item.setQuantity(1);
            item.setOriginalUnitPrice(entry.getOriginalAmount().getValue());
            item.setOriginalTotalPrice(item.getOriginalUnitPrice());
            item.setTotalDiscountAmount(entry.getDiscountAmount().getValue());
            item.setFinalTotalAmount(item.getOriginalTotalPrice().subtract(item.getTotalDiscountAmount()));
            item.setPromotions(entry.getAppliedPromotion());
            result.getLineItems().add(item);
        }

        return result;
    }


    private RatingResultBuilder() {
    }
}
