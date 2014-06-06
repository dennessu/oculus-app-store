/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.catalog.spec.enums.ItemType;
import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.promotion.*;
import com.junbo.catalog.spec.model.promotion.criterion.Criterion;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.common.util.Func;
import com.junbo.rating.common.util.Utils;
import com.junbo.rating.core.RatingService;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.core.handler.HandlerRegister;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.fusion.*;
import com.junbo.rating.spec.model.Money;
import com.junbo.rating.spec.model.RatableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by lizwu on 2/7/14.
 */
public abstract class RatingServiceSupport implements RatingService<PriceRatingContext> {
    @Autowired
    @Qualifier("ratingCatalogGateway")
    protected CatalogGateway catalogGateway;

    protected void fillOffer(PriceRatingContext context) {
        for (RatableItem item : context.getItems()) {
            item.setOffer(catalogGateway.getOffer(item.getOfferId(), context.getTimestamp()));
            validateLineItem(item, context.getTimestamp());
        }
    }

    private void validateLineItem(RatableItem item, String timestamp) {
        if (item.getQuantity() > 1 && containsDigitalGoods(item.getOffer(), timestamp)){
            throw AppErrors.INSTANCE.incorrectQuantity(item.getOfferId().toString(), item.getQuantity()).exception();
        }
    }

    private boolean containsDigitalGoods(RatingOffer offer, String timestamp) {
        for (LinkedEntry entry : offer.getItems()) {
            Item item = catalogGateway.getItem(entry.getEntryId());
            if (item.getType().equalsIgnoreCase(ItemType.DIGITAL.name())) {
                return true;
            }
        }
        for (LinkedEntry entry : offer.getSubOffers()) {
            RatingOffer subOffer = catalogGateway.getOffer(entry.getEntryId(), timestamp);
            if (containsDigitalGoods(subOffer, timestamp)) {
                return true;
            }
        }
        return false;
    }

    protected Map<PromotionType, Set<PromotionRevision>> getPromotionRulesByTypes(PromotionType... types) {
        Map<PromotionType, Set<PromotionRevision>> result = new HashMap<>();

        //call catalog to get all currently effective promotions
        List<PromotionRevision> promotions = catalogGateway.getPromotions();

        for (PromotionRevision promotion : promotions) {
            if (Arrays.asList(types).contains(promotion.getType())) {
                if (!result.containsKey(promotion.getType())) {
                    result.put(promotion.getType(), new HashSet<PromotionRevision>());
                }
                //sort criteria for each promotion
                Collections.sort(promotion.getCriteria(), new Comparator<Criterion>() {
                    @Override
                    public int compare(Criterion c1, Criterion c2) {
                        return c1.getPredicate().getSequence() - c2.getPredicate().getSequence();
                    }
                });
                result.get(promotion.getType()).add(promotion);
            }
        }

        return result;
    }

    protected void filterByCurrency(final PriceRatingContext context) {
        discardRule(context, new Func<PromotionRevision, Boolean>() {
            @Override
            public Boolean execute(PromotionRevision promotion) {
                return promotion.getCurrency().equalsIgnoreCase(context.getCurrency().getCode());
            }
        });
    }

    protected void filterByEffectiveDate(PriceRatingContext context) {
        discardRule(context, new Func<PromotionRevision, Boolean>() {
            @Override
            public Boolean execute(PromotionRevision promotion) {
                return promotion.isEffective(Utils.now());
            }
        });
    }

    private void discardRule(PriceRatingContext context, Func<PromotionRevision, Boolean> predicate) {
        for (Set<PromotionRevision> entry : context.getRules().values()) {
            Iterator<PromotionRevision> iterator = entry.iterator();
            while (iterator.hasNext()) {
                PromotionRevision rule = iterator.next();

                // discard candidate that doesn't match criteria
                if (!predicate.execute(rule)) {
                    iterator.remove();
                }
            }
        }
    }

    protected void findCandidates(PriceRatingContext context) {
        for (RatableItem item : context.getItems()) {
            context.setCurrentItem(item);
            context.getCandidates().put(item.getOfferId(), filterByCriteria(context));
        }
        //set currentItem to NON_SPECIFIC_ITEM
    }



    private Set<PromotionRevision> filterByCriteria(PriceRatingContext context) {
        Set<PromotionRevision> promotions = context.getRules().get(PromotionType.OFFER_PROMOTION);
        if (promotions == null) {
            return Collections.<PromotionRevision>emptySet();
        }

        Set<PromotionRevision> candidates = new HashSet<>();
        for (PromotionRevision promotion : promotions) {
            if (validatePromotion(promotion, context)) {
                candidates.add(promotion);
            }
        }

        return candidates;
    }

    protected boolean validatePromotion(PromotionRevision promotion, PriceRatingContext context) {
        for (Criterion criterion : promotion.getCriteria()) {
            if (!HandlerRegister.getHandler(criterion.getPredicate().toString()).validate(criterion, context)) {
                return false;
            }
        }

        return true;
    }

    protected Money getPrice(RatingOffer offer, String country, String currency) {
        Price price = offer.getPrice();
        if (price == null) {
            return Money.NOT_FOUND;
        }

        if (PriceType.FREE.name().equalsIgnoreCase(price.getPriceType())) {
            return new Money(BigDecimal.ZERO, currency);
        }

        if (price.getPrices() == null || !price.getPrices().containsKey(country)) {
            return Money.NOT_FOUND;
        }

        Map<String, BigDecimal> prices = price.getPrices().get(country);
        if (!prices.containsKey(currency)) {
            return Money.NOT_FOUND;
        }

        return new Money(prices.get(currency), currency);
    }

    protected Money applyBenefit(Money original, Benefit benefit) {
        Money result = new Money(original.getCurrency());
        switch (benefit.getType()) {
            case FLAT_DISCOUNT:
                result.setValue(benefit.getValue());
                break;
            case RATIO_DISCOUNT:
                result.setValue(original.getValue().multiply(benefit.getValue()));
                break;
            case FIXED_PRICE:
                result.setValue(original.getValue().subtract(benefit.getValue()));
                break;
            default:
                return Money.NOT_FOUND;
            }
        return result;
    }
}
