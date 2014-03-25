/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.core.builder.OfferRatingResultBuilder;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.Money;
import com.junbo.rating.spec.model.RatableItem;
import com.junbo.rating.spec.model.RatingResultEntry;
import com.junbo.rating.spec.model.request.OfferRatingRequest;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lizwu on 2/7/14.
 */
public class OfferRatingService extends RatingServiceSupport{

    public OfferRatingRequest offerRating(RatingContext context) {
        rate(context);
        return OfferRatingResultBuilder.build(context);
    }

    @Override
    public void rate(RatingContext context) {
        initContext(context);
        filterByCurrency(context);
        filterByEffectiveDate(context);
        findCandidates(context);
        findBestPrice(context);
    }

    private void initContext(RatingContext context) {
        context.getRules().putAll(getPromotionRulesByTypes(PromotionType.OFFER_PROMOTION));
        fillOffer(context);
    }

    private void findBestPrice(RatingContext context) {
        Map<Long, Set<Promotion>> candidates = context.getCandidates();
        String country = context.getCountry();
        Currency currency = context.getCurrency();

        for (RatableItem item : context.getItems()) {
            Long offerId = item.getOfferId();
            Set<Promotion> promotions = candidates.get(offerId) == null?
                    new HashSet<Promotion>() : candidates.get(offerId);

            Money originalPrice = getPrice(item.getOffer(), country, currency.getCode());
            if (originalPrice == Money.NOT_FOUND) {
                throw AppErrors.INSTANCE.priceNotFound(item.getOfferId().toString()).exception();
            }

            Money bestBenefit = new Money(BigDecimal.ZERO, originalPrice.getCurrency());

            RatingResultEntry entry = new RatingResultEntry();
            entry.setOfferId(item.getOfferId());
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
            bestBenefit.rounding(currency.getDigits());
            entry.setDiscountAmount(bestBenefit);
            context.getEntries().add(entry);
        }
    }
}
