/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.builder;

import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.spec.model.RatingResultEntry;
import com.junbo.rating.spec.model.request.*;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Created by lizwu on 2/26/14.
 */
public class RatingResultBuilder {

    public static RatingRequest buildForOrder(PriceRatingContext context) {
        RatingRequest result = new RatingRequest();
        result.setUserId(context.getUserId());
        result.setCurrency(context.getCurrency().getCode());
        result.setTime(context.getTimestamp());
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
        RatingSummary ratingSummary = new RatingSummary();
        ratingSummary.setDiscountAmount(context.getOrderResult().getDiscountAmount().getValue());
        BigDecimal finalTotalAmount = context.getOrderResult().getOriginalAmount().subtract(
                context.getOrderResult().getDiscountAmount()).getValue();
        ratingSummary.setFinalAmount(finalTotalAmount);
        ratingSummary.setPromotion(context.getOrderResult().getAppliedPromotion());
        result.setRatingSummary(ratingSummary);

        //build shipping fee calculation results
        ShippingSummary shippingSummary = new ShippingSummary();
        shippingSummary.setTotalShippingFee(context.getShippingResult().getShippingFee());
        shippingSummary.setPromotion(context.getShippingResult().getAppliedPromotion());
        result.setShippingSummary(shippingSummary);

        return result;
    }

    public static RatingRequest buildForOffers(PriceRatingContext context) {
        RatingRequest result = new RatingRequest();
        result.setUserId(context.getUserId());
        result.setCurrency(context.getCurrency().getCode());
        result.setTime(context.getTimestamp());
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
