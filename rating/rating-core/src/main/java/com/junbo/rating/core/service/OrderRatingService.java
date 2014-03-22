/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.core.builder.OrderRatingResultBuilder;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.fusion.LinkedEntry;
import com.junbo.rating.spec.fusion.RatingOffer;
import com.junbo.rating.spec.model.*;
import com.junbo.rating.spec.model.request.OrderRatingRequest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lizwu on 2/7/14.
 */
public class OrderRatingService extends RatingServiceSupport{

    public OrderRatingRequest orderRating(RatingContext context) {
        rate(context);
        return OrderRatingResultBuilder.build(context);
    }

    @Override
    public void rate(RatingContext context) {
        initContext(context);
        filterByCurrency(context);
        filterByEffectiveDate(context);
        findCandidates(context);
        findBestPrice(context);
        calculateOrderLevelPromotion(context);
        calculateShippingFee(context);
    }

    private void initContext(RatingContext context) {
        context.getRules().putAll(
                getPromotionRulesByTypes(PromotionType.OFFER_PROMOTION, PromotionType.ORDER_PROMOTION));
        fillOffer(context);
    }

    private void findBestPrice(RatingContext context) {
        Map<Long, Set<Promotion>> candidates = context.getCandidates();
        String country = context.getCountry();
        String currency = context.getCurrency();

        //TODO: this logic will be changed after adding new kinds of offer level promotions
        for (RatableItem item : context.getItems()) {
            Long offerId = item.getOfferId();
            Set<Promotion> promotions = candidates.get(offerId) == null?
                    new HashSet<Promotion>() : candidates.get(offerId);

            Money originalPrice = getPrice(item.getOffer(), country, currency);
            if (originalPrice == Money.NOT_FOUND) {
                throw AppErrors.INSTANCE.priceNotFound(item.getOfferId().toString()).exception();
            }

            Money bestBenefit = new Money(BigDecimal.ZERO, originalPrice.getCurrency());

            RatingResultEntry entry = new RatingResultEntry();
            entry.setOfferId(item.getOfferId());
            entry.setQuantity(item.getQuantity());
            entry.setShippingMethodId(item.getShippingMethodId());
            entry.setOriginalAmount(originalPrice);
            entry.setAppliedPromotion(new HashSet<Long>());
            for (Promotion promotion : promotions) {
                if (promotion.getBenefit() == null) {
                    continue;
                }

                Money currentBenefit = applyBenefit(originalPrice, promotion.getBenefit());
                if (currentBenefit.greaterThan(bestBenefit)) {
                    bestBenefit = currentBenefit;
                    entry.getAppliedPromotion().add(promotion.getId());
                }
            }
            entry.setDiscountAmount(bestBenefit);
            context.getEntries().add(entry);
        }
    }

    private void calculateOrderLevelPromotion(RatingContext context) {
        Money totalAmount = new Money(BigDecimal.ZERO, context.getCurrency());
        for (RatingResultEntry entry : context.getEntries()) {
            //calculate the total amount of line items in current order
            totalAmount = totalAmount.add(
                    entry.getOriginalAmount().subtract(entry.getDiscountAmount()).multiple(entry.getQuantity()));
        }

        OrderResultEntry result = new OrderResultEntry();
        result.setOriginalAmount(totalAmount);
        //for criterion validation
        context.setOrderResult(result);

        Money bestBenefit = new Money(BigDecimal.ZERO, context.getCurrency());

        Set<Promotion> candidates = context.getRules().get(PromotionType.ORDER_PROMOTION) == null?
                new HashSet<Promotion>() : context.getRules().get(PromotionType.ORDER_PROMOTION);

        for (Promotion promotion : candidates) {
            //not benefit configured
            if (promotion.getBenefit() == null) {
                continue;
            }

            //criterion is not satisfied
            if (!validatePromotion(promotion, context)) {
                continue;
            }

            Money currentBenefit = applyBenefit(totalAmount, promotion.getBenefit());
            if (currentBenefit.greaterThan(bestBenefit)) {
                bestBenefit = currentBenefit;
                result.setAppliedPromotion(promotion.getId());
            }
        }
        result.setDiscountAmount(bestBenefit);
        context.setOrderResult(result);
    }

    private void calculateShippingFee(RatingContext context) {
        BigDecimal shippingFee = BigDecimal.ZERO;
        Map<Long, Integer> shippingDetail = new HashMap<Long, Integer>();

        for (RatableItem item : context.getItems()) {
            if (item.getShippingMethodId() == null) {
                continue;
            }
            if (!shippingDetail.containsKey(item.getShippingMethodId())) {
                shippingDetail.put(item.getShippingMethodId(), 0);
            }
            shippingDetail.put(item.getShippingMethodId(),
                    shippingDetail.get(item.getShippingMethodId()) + getQuantity(item.getOffer(), item.getQuantity()));
        }

        for (Long shippingMethodId : shippingDetail.keySet()) {
            int quantity = shippingDetail.get(shippingMethodId);
            ShippingMethod shippingMethod = catalogGateway.getShippingMethod(shippingMethodId);
            shippingFee = shippingFee.add(shippingMethod.getBasePrice());
            if (quantity > shippingMethod.getBaseUnit()) {
                int additionalUnit = quantity > shippingMethod.getCapUnit() ?
                        shippingMethod.getCapUnit() - shippingMethod.getBaseUnit()
                        : quantity - shippingMethod.getBaseUnit();
                shippingFee = shippingFee.add(
                        shippingMethod.getAdditionalPrice().multiply(new BigDecimal(additionalUnit)));
            }
        }

        ShippingResultEntry result = new ShippingResultEntry();
        result.setShippingFee(shippingFee);
        context.setShippingResult(result);
    }

    private int getQuantity(RatingOffer ratingOffer, int quantity) {
        int result = 0;

        for (LinkedEntry entry : ratingOffer.getItems()) {
            Item item = catalogGateway.getItem(entry.getEntryId());
            if ("Physical".equalsIgnoreCase(item.getType())) {
                result += entry.getQuantity();
            }
        }

        for (LinkedEntry entry : ratingOffer.getSubOffers()) {
            RatingOffer offer = catalogGateway.getOffer(entry.getEntryId());
            result += getQuantity(offer, entry.getQuantity());
        }

        return quantity * result;
    }
}
