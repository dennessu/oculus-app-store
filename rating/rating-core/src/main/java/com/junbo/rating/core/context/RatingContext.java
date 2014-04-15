/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.context;

import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.model.*;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.request.OfferRatingItem;
import com.junbo.rating.spec.model.request.OfferRatingRequest;
import com.junbo.rating.spec.model.request.RatingItem;
import com.junbo.rating.spec.model.request.RatingRequest;

import java.util.*;

/**
 * Created by lizwu on 2/12/14.
 */
public class RatingContext {
    private Long userId;
    private Currency currency;
    private Map<String, String> couponCodes;
    private Set<RatableItem> items;
    private Map<Long, Set<PromotionRevision>> candidates;
    private Map<PromotionType, Set<PromotionRevision>> rules;

    private RatableItem currentItem;

    private Set<RatingResultEntry> entries;

    private OrderResultEntry orderResult;

    private ShippingResultEntry shippingResult;
    //TODO: violations

    public RatingContext() {
        items = new HashSet<RatableItem>();
        couponCodes = new HashMap<String, String>();
        candidates = new HashMap<Long, Set<PromotionRevision>>();
        rules = new HashMap<PromotionType, Set<PromotionRevision>>();
        entries = new HashSet<RatingResultEntry>();
    }

    public void fromRequest(OfferRatingRequest request) {
        this.userId = request.getUserId();

        Currency currency = Currency.findByCode(request.getCurrency());
        if (currency == null) {
            throw AppErrors.INSTANCE.currencyNotExist(request.getCurrency()).exception();
        }

        this.currency = Currency.findByCode(request.getCurrency());

        for (OfferRatingItem offerRatingItem : request.getOffers()) {
            RatableItem item = new RatableItem();
            item.setOfferId(offerRatingItem.getOfferId());
            this.items.add(item);
        }
    }

    public void fromRequest(RatingRequest request) {
        this.userId = request.getUserId();

        Currency currency = Currency.findByCode(request.getCurrency());
        if (currency == null) {
            throw AppErrors.INSTANCE.currencyNotExist(request.getCurrency()).exception();
        }

        this.currency = currency;

        for (String coupon : request.getCouponCodes()) {
            couponCodes.put(coupon, null);
        }

        for (RatingItem ratingItem : request.getLineItems()) {
            RatableItem item = new RatableItem();
            item.setOfferId(ratingItem.getOfferId());

            if (request.isReadyToBuy()) {
                item.setQuantity(ratingItem.getQuantity());
                item.setShippingMethodId(
                        ratingItem.getShippingMethodId() == null ?
                                request.getShippingMethodId() : ratingItem.getShippingMethodId());
            }

            items.add(item);
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Map<String, String> getCouponCodes() {
        return couponCodes;
    }

    public void setCouponCodes(Map<String, String> couponCodes) {
        this.couponCodes = couponCodes;
    }

    public Set<RatableItem> getItems() {
        return items;
    }

    public void setItems(Set<RatableItem> items) {
        this.items = items;
    }

    public Map<Long, Set<PromotionRevision>> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<Long, Set<PromotionRevision>> candidates) {
        this.candidates = candidates;
    }

    public Map<PromotionType, Set<PromotionRevision>> getRules() {
        return rules;
    }

    public void setRules(Map<PromotionType, Set<PromotionRevision>> rules) {
        this.rules = rules;
    }

    public RatableItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(RatableItem currentItem) {
        this.currentItem = currentItem;
    }

    public Set<RatingResultEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<RatingResultEntry> entries) {
        this.entries = entries;
    }

    public OrderResultEntry getOrderResult() {
        return orderResult;
    }

    public void setOrderResult(OrderResultEntry orderResult) {
        this.orderResult = orderResult;
    }

    public ShippingResultEntry getShippingResult() {
        return shippingResult;
    }

    public void setShippingResult(ShippingResultEntry shippingResult) {
        this.shippingResult = shippingResult;
    }
}
