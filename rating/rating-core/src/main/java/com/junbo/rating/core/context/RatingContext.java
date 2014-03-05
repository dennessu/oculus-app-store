/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.context;

import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.spec.model.OrderResultEntry;
import com.junbo.rating.spec.model.RatableItem;
import com.junbo.rating.spec.model.RatingResultEntry;
import com.junbo.rating.spec.model.request.OfferRatingItem;
import com.junbo.rating.spec.model.request.OfferRatingRequest;
import com.junbo.rating.spec.model.request.OrderRatingItem;
import com.junbo.rating.spec.model.request.OrderRatingRequest;

import java.util.*;

/**
 * Created by lizwu on 2/12/14.
 */
public class RatingContext {
    private Long userId;
    private String country;
    private String currency;
    private Map<String, String> couponCodes;
    private Set<RatableItem> items;
    private Map<Long, Set<Promotion>> candidates;
    private Map<PromotionType, Set<Promotion>> rules;

    private RatableItem currentItem;

    private Set<RatingResultEntry> entries;

    private OrderResultEntry orderResultEntry;
    //TODO: shipping fee and violations

    public RatingContext() {
        items = new HashSet<RatableItem>();
        couponCodes = new HashMap<String, String>();
        candidates = new HashMap<Long, Set<Promotion>>();
        rules = new HashMap<PromotionType, Set<Promotion>>();
        entries = new HashSet<RatingResultEntry>();
    }

    public void fromRequest(OfferRatingRequest request) {
        this.userId = request.getUserId();
        this.country = request.getCountry();
        this.currency = request.getCurrency();

        for (OfferRatingItem offerRatingItem : request.getOffers()) {
            RatableItem item = new RatableItem();
            item.setOfferId(offerRatingItem.getOfferId());
            this.items.add(item);
        }
    }

    public void fromRequest(OrderRatingRequest request) {
        this.userId = request.getUserId();
        this.country = request.getCountry();
        this.currency = request.getCurrency();

        for (String coupon : request.getCouponCodes()) {
            couponCodes.put(coupon, null);
        }

        for (OrderRatingItem orderRatingItem : request.getLineItems()) {
            RatableItem item = new RatableItem();
            item.setOfferId(orderRatingItem.getOfferId());
            item.setQuantity(orderRatingItem.getQuantity());
            items.add(item);
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
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

    public Map<Long, Set<Promotion>> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<Long, Set<Promotion>> candidates) {
        this.candidates = candidates;
    }

    public Map<PromotionType, Set<Promotion>> getRules() {
        return rules;
    }

    public void setRules(Map<PromotionType, Set<Promotion>> rules) {
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

    public OrderResultEntry getOrderResultEntry() {
        return orderResultEntry;
    }

    public void setOrderResultEntry(OrderResultEntry orderResultEntry) {
        this.orderResultEntry = orderResultEntry;
    }
}
