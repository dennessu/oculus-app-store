/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.builder;

import com.junbo.rating.common.util.Utils;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.spec.model.RatingResultEntry;
import com.junbo.rating.spec.model.priceRating.*;
import com.junbo.rating.spec.model.subscription.SubsRatingRequest;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Created by lizwu on 2/26/14.
 */
public class RatingResultBuilder {

    public static RatingRequest buildForOrder(PriceRatingContext context) {
        RatingRequest result = new RatingRequest();
        result.setUserId(context.getUserId());
        result.setCountry(context.getCountry());
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

            item.setPreOrderPrice(entry.getPreOrderPrice());
            item.setOriginalUnitPrice(entry.getOriginalPrice());
            item.setOriginalTotalPrice(entry.getOriginalPrice().multiply(new BigDecimal(entry.getQuantity())));

            BigDecimal totalDiscount = entry.getDiscountAmount().multiply(new BigDecimal(entry.getQuantity()));
            item.setTotalDiscountAmount(Utils.rounding(totalDiscount, context.getCurrency().getDigits()));
            item.setPromotions(entry.getAppliedPromotion());

            item.setFinalTotalAmount(item.getOriginalTotalPrice().subtract(item.getTotalDiscountAmount()));
            item.setDeveloperRevenue(Utils.rounding(item.getFinalTotalAmount().multiply(entry.getDeveloperRatio()),
                    context.getCurrency().getDigits()));

            result.getLineItems().add(item);
        }

        //build order level results
        RatingSummary ratingSummary = new RatingSummary();
        ratingSummary.setDiscountAmount(Utils.rounding(context.getOrderResult().getDiscountAmount(),
                context.getCurrency().getDigits()));
        ratingSummary.setPromotion(context.getOrderResult().getAppliedPromotion());

        BigDecimal finalTotalAmount = context.getOrderResult().getOriginalAmount().subtract(
                context.getOrderResult().getDiscountAmount());
        ratingSummary.setFinalAmount(finalTotalAmount);

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
        result.setCountry(context.getCountry());
        result.setCurrency(context.getCurrency().getCode());
        result.setTime(context.getTimestamp());
        result.setLineItems(new HashSet<RatingItem>());
        for (RatingResultEntry entry : context.getEntries()) {
            RatingItem item = new RatingItem();
            item.setOfferId(entry.getOfferId());
            item.setQuantity(1);
            item.setPreOrderPrice(entry.getPreOrderPrice());
            item.setOriginalUnitPrice(entry.getOriginalPrice());
            item.setOriginalTotalPrice(item.getOriginalUnitPrice());
            item.setTotalDiscountAmount(Utils.rounding(entry.getDiscountAmount(), context.getCurrency().getDigits()));
            item.setFinalTotalAmount(item.getOriginalTotalPrice().subtract(item.getTotalDiscountAmount()));
            item.setPromotions(entry.getAppliedPromotion());
            result.getLineItems().add(item);
        }

        return result;
    }

    public static SubsRatingRequest buildForSubs(SubsRatingContext context) {
        SubsRatingRequest result = new SubsRatingRequest();
        result.setOfferId(context.getOfferId());
        result.setCountry(context.getCountry());
        result.setCurrency(context.getCurrency().getCode());
        result.setType(context.getType());
        result.setAmount(context.getAmount());
        return result;
    }

    private RatingResultBuilder() {
    }
}
