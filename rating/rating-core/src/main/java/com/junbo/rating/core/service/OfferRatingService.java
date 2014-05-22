/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.Money;
import com.junbo.rating.spec.model.RatableItem;
import com.junbo.rating.spec.model.RatingResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Offer Rating Service.
 */
public class OfferRatingService extends RatingServiceSupport{
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferRatingService.class);

    @Override
    public void rate(PriceRatingContext context) {
        initContext(context);
        filterByCurrency(context);
        filterByEffectiveDate(context);
        findCandidates(context);
        findBestPrice(context);
    }

    private void initContext(PriceRatingContext context) {
        context.getRules().putAll(getPromotionRulesByTypes(PromotionType.OFFER_PROMOTION));
        fillOffer(context);
    }

    private void findBestPrice(PriceRatingContext context) {
        Map<Long, Set<PromotionRevision>> candidates = context.getCandidates();
        Currency currency = context.getCurrency();

        for (RatableItem item : context.getItems()) {
            Long offerId = item.getOfferId();
            Set<PromotionRevision> promotions = candidates.get(offerId) == null?
                    new HashSet<PromotionRevision>() : candidates.get(offerId);

            Money originalPrice = getPrice(item.getOffer(), currency.getCode());
            if (originalPrice == Money.NOT_FOUND) {
                LOGGER.error("Price of Offer [" + offerId + "] is not found for Currency [" + currency + "].");
                throw AppErrors.INSTANCE.priceNotFound(item.getOfferId().toString()).exception();
            }

            Money bestBenefit = new Money(BigDecimal.ZERO, originalPrice.getCurrency());

            RatingResultEntry entry = new RatingResultEntry();
            entry.setOfferId(item.getOfferId());
            entry.setOriginalAmount(originalPrice);
            entry.setAppliedPromotion(new HashSet<Long>());
            for (PromotionRevision promotion : promotions) {
                if (promotion.getBenefit() == null) {
                    continue;
                }

                Money currentBenefit = applyBenefit(originalPrice, promotion.getBenefit());
                if (currentBenefit.greaterThan(bestBenefit)) {
                    bestBenefit = currentBenefit;
                    entry.getAppliedPromotion().add(promotion.getRevisionId());
                }
            }
            bestBenefit.rounding(currency.getDigits());
            entry.setDiscountAmount(bestBenefit);
            context.getEntries().add(entry);
        }
    }
}
