/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.model.Currency;
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
public class OfferRatingService extends RatingServiceSupport {
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
        Map<String, Set<PromotionRevision>> candidates = context.getCandidates();
        Currency currency = context.getCurrency();

        for (RatableItem item : context.getItems()) {
            String offerId = item.getOfferId();
            Set<PromotionRevision> promotions = candidates.get(offerId) == null?
                    new HashSet<PromotionRevision>() : candidates.get(offerId);

            BigDecimal originalPrice = getPrice(item.getOffer().getPrice(), context.getCountry(), currency.getCode());
            if (originalPrice == Constants.PRICE_NOT_FOUND) {
                LOGGER.error("Price of Offer [" + offerId + "] is not found for Currency [" + currency + "].");
                throw AppErrors.INSTANCE.missingConfiguration("price").exception();
            }

            BigDecimal bestBenefit = BigDecimal.ZERO;

            RatingResultEntry entry = new RatingResultEntry();
            entry.setOfferId(item.getOfferId());
            entry.setPreOrderPrice(getPreOrderPrice(item.getOffer(), context.getCountry(), currency.getCode()));
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
}
