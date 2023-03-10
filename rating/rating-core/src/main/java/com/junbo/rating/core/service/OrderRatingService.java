/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.catalog.spec.enums.ItemType;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.clientproxy.OrganizationGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Order Rating Service.
 */
public class OrderRatingService extends RatingServiceSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRatingService.class);

    @Autowired
    @Qualifier("ratingOrganizationGateway")
    protected OrganizationGateway organizationGateway;

    @Override
    public void rate(PriceRatingContext context) {
        initContext(context);
        validateLineItems(context);
        filterByCurrency(context);
        filterByEffectiveDate(context);
        findCandidates(context);
        findBestPrice(context);
        calculateOrderLevelPromotion(context);
        calculateShippingFee(context);
    }

    private void initContext(PriceRatingContext context) {
        context.getRules().putAll(
                getPromotionRulesByTypes(PromotionType.OFFER_PROMOTION, PromotionType.ORDER_PROMOTION));
        fillOffer(context);
    }

    private void findBestPrice(PriceRatingContext context) {
        Map<String, Set<PromotionRevision>> candidates = context.getCandidates();
        Currency currency = context.getCurrency();

        //TODO: this logic will be changed after adding new kinds of offer level promotions
        for (RatableItem item : context.getItems()) {
            String offerId = item.getOfferId();
            Set<PromotionRevision> promotions = candidates.get(offerId) == null?
                    new HashSet<PromotionRevision>() : candidates.get(offerId);

            BigDecimal originalPrice = getPrice(item.getOffer().getPrice(), context.getCountry(), currency.getCurrencyCode());
            if (originalPrice == Constants.PRICE_NOT_FOUND) {
                LOGGER.error("Price of Offer [" + offerId + "] is not found for CurrencyInfo [" + currency + "].");
                throw AppErrors.INSTANCE.missingConfiguration("price").exception();
            }

            BigDecimal bestBenefit = BigDecimal.ZERO;

            RatingResultEntry entry = new RatingResultEntry();
            entry.setOfferId(item.getOfferId());
            entry.setQuantity(item.getQuantity());
            entry.setShippingMethodId(item.getShippingMethodId());
            //entry.setDeveloperRatio(item.getOffer().getDeveloperRatio());
            Double developerRatio = organizationGateway.getOrganization(item.getOffer().getOrganizationId()).getPublisherRevenueRatio();
            entry.setDeveloperRatio(developerRatio == null? BigDecimal.ZERO: BigDecimal.valueOf(developerRatio));
            entry.setPreOrderPrice(getPreOrderPrice(item.getOffer(), context.getCountry(), currency.getCurrencyCode()));
            entry.setOriginalPrice(originalPrice);
            entry.setAppliedPromotion(new HashSet<String>());
            for (PromotionRevision promotion : promotions) {
                if (promotion.getBenefit() == null) {
                    continue;
                }

                BigDecimal currentBenefit = applyBenefit(originalPrice, promotion.getBenefit());
                if (currentBenefit.compareTo(bestBenefit) >= 0) {
                    bestBenefit = currentBenefit;
                    entry.getAppliedPromotion().add(promotion.getRevisionId());
                }
            }
            entry.setDiscountAmount(bestBenefit);
            context.getEntries().add(entry);
        }
    }

    private void calculateOrderLevelPromotion(PriceRatingContext context) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (RatingResultEntry entry : context.getEntries()) {
            //calculate the total amount of line items in current order
            totalAmount = totalAmount.add(
                    entry.getOriginalPrice().subtract(entry.getDiscountAmount()).multiply(new BigDecimal(entry.getQuantity())));
        }

        OrderResultEntry result = new OrderResultEntry();
        result.setOriginalAmount(totalAmount);
        //for criterion validation
        context.setOrderResult(result);

        BigDecimal bestBenefit = BigDecimal.ZERO;

        Set<PromotionRevision> candidates = context.getRules().get(PromotionType.ORDER_PROMOTION) == null?
                new HashSet<PromotionRevision>() : context.getRules().get(PromotionType.ORDER_PROMOTION);

        for (PromotionRevision promotion : candidates) {
            //not benefit configured
            if (promotion.getBenefit() == null) {
                continue;
            }

            //criterion is not satisfied
            if (!validatePromotion(promotion, context)) {
                continue;
            }

            BigDecimal currentBenefit = applyBenefit(totalAmount, promotion.getBenefit());
            if (currentBenefit.compareTo(bestBenefit) >= 0) {
                bestBenefit = currentBenefit;
                result.setAppliedPromotion(promotion.getRevisionId());
            }
        }
        result.setDiscountAmount(bestBenefit);
        context.setOrderResult(result);
    }

    private void calculateShippingFee(PriceRatingContext context) {
        ShippingResultEntry result = new ShippingResultEntry();
        result.setShippingFee(BigDecimal.ZERO);

        for (RatableItem item : context.getItems()) {
            if (!containsSpecificTypeGoods(item.getOffer(), context.getTimestamp(), ItemType.PHYSICAL)) {
                continue;
            }

            String shippingMethodId = item.getShippingMethodId() == null?
                    context.getDefaultShippingMethod() : item.getShippingMethodId();
            if (shippingMethodId == null) {
                LOGGER.warn("Missing shipping method for Offer " + item.getOfferId());
                continue;
            }

            //todo
            result.setShippingFee(BigDecimal.TEN);
        }

        context.setShippingResult(result);
    }
}
