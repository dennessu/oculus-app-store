/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.catalog.spec.model.promotion.Benefit;
import com.junbo.catalog.spec.model.promotion.Criterion;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.common.util.Func;
import com.junbo.rating.common.util.Utils;
import com.junbo.rating.core.RatingService;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.core.handler.HandlerRegister;
import com.junbo.rating.spec.fusion.*;
import com.junbo.rating.spec.model.Money;
import com.junbo.rating.spec.model.RatableItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by lizwu on 2/7/14.
 */
public abstract class RatingServiceSupport implements RatingService{
    @Autowired
    protected CatalogGateway catalogGateway;

    protected void fillOffer(RatingContext context) {
        for (RatableItem item : context.getItems()) {
            item.setOffer(catalogGateway.getOffer(item.getOfferId()));
        }
    }

    protected Map<PromotionType, Set<Promotion>> getPromotionRulesByTypes(PromotionType... types) {
        Map<PromotionType, Set<Promotion>> result = new HashMap<PromotionType, Set<Promotion>>();

        //call catalog to get all currently effective promotions
        List<Promotion> promotions = catalogGateway.getPromotions();

        for (Promotion promotion : promotions) {
            if (Arrays.asList(types).contains(promotion.getType())) {
                if (!result.containsKey(promotion.getType())) {
                    result.put(promotion.getType(), new HashSet<Promotion>());
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

    protected void filterByCurrency(final RatingContext context) {
        discardRule(context, new Func<Promotion, Boolean>() {
            @Override
            public Boolean execute(Promotion promotion) {
                return promotion.getCurrency().equals(context.getCurrency());
            }
        });
    }

    protected void filterByEffectiveDate(RatingContext context) {
        discardRule(context, new Func<Promotion, Boolean>() {
            @Override
            public Boolean execute(Promotion promotion) {
                return promotion.isEffective(Utils.now());
            }
        });
    }

    private void discardRule(RatingContext context, Func<Promotion, Boolean> predicate) {
        for (Set<Promotion> entry : context.getRules().values()) {
            Iterator<Promotion> iterator = entry.iterator();
            while (iterator.hasNext()) {
                Promotion rule = iterator.next();

                // discard candidate that doesn't match criteria
                if (!predicate.execute(rule)) {
                    iterator.remove();
                }
            }
        }
    }

    protected void findCandidates(RatingContext context) {
        for (RatableItem item : context.getItems()) {
            context.setCurrentItem(item);
            context.getCandidates().put(item.getOfferId(), filterByCriteria(context));
        }
        //set currentItem to NON_SPECIFIC_ITEM
    }



    private Set<Promotion> filterByCriteria(RatingContext context) {
        Set<Promotion> promotions = context.getRules().get(PromotionType.OFFER_PROMOTION);
        if (promotions == null) {
            return Collections.<Promotion>emptySet();
        }

        Set<Promotion> candidates = new HashSet<Promotion>();
        for (Promotion promotion : promotions) {
            if (validatePromotion(promotion, context)) {
                candidates.add(promotion);
            }
        }

        return candidates;
    }

    protected boolean validatePromotion(Promotion promotion, RatingContext context) {
        for (Criterion criterion : promotion.getCriteria()) {
            if (!HandlerRegister.isSatisfied(criterion, context)) {
                return false;
            }
        }

        return true;
    }

    protected Money getPrice(RatingOffer offer, String country, String currency) {
        Map<String, Price> priceMap = offer.getPrices();
        if (!priceMap.containsKey(country)) {
            return Money.NOT_FOUND;
        }

        Price price = priceMap.get(country);
        if (currency.equalsIgnoreCase(price.getCurrency())) {
            return new Money(price.getPrice(), price.getCurrency());
        }
        return Money.NOT_FOUND;
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
