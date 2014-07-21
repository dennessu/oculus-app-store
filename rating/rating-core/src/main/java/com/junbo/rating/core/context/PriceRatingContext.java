/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.context;

import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.common.util.Utils;
import com.junbo.rating.core.builder.Builder;
import com.junbo.rating.spec.model.*;
import com.junbo.rating.spec.model.priceRating.RatingItem;
import com.junbo.rating.spec.model.priceRating.RatingRequest;
import com.junbo.rating.spec.model.priceRating.RatingSummary;
import com.junbo.rating.spec.model.priceRating.ShippingSummary;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lizwu on 5/22/14.
 */
public class PriceRatingContext extends RatingContext implements Builder<RatingRequest> {
    private Long userId;
    private Map<String, String> couponCodes;
    private Set<RatableItem> items;
    private String timestamp;
    private Map<String, Set<PromotionRevision>> candidates;
    private Map<PromotionType, Set<PromotionRevision>> rules;

    private RatableItem currentItem;
    private Set<RatingResultEntry> entries;

    private OrderResultEntry orderResult;

    private String defaultShippingMethod;
    private ShippingResultEntry shippingResult;

    public PriceRatingContext() {
        items = new HashSet<>();
        couponCodes = new HashMap<>();
        candidates = new HashMap<>();
        rules = new HashMap<>();
        entries = new HashSet<>();
    }

    @Override
    public void fromRequest(RatingRequest request) {
        super.setRatingType(request.getIncludeCrossOfferPromos() ? RatingType.ORDER : RatingType.OFFER);
        super.setCountry(request.getCountry());

        Currency currency = new Currency();
        currency.setCurrencyCode(request.getCurrency());
        currency.setNumberAfterDecimal(Constants.DEFAULT_CURRENCY.equalsIgnoreCase(currency.getCurrencyCode())? 0
                : CurrencyInfo.findByCode(currency.getCurrencyCode()).getDigits());
        super.setCurrency(currency);


        this.userId = request.getUserId();
        this.timestamp = request.getTime();
        this.defaultShippingMethod = request.getShippingMethodId();

        for (String coupon : request.getCoupons()) {
            couponCodes.put(coupon, null);
        }

        for (RatingItem ratingItem : request.getLineItems()) {
            RatableItem item = new RatableItem();
            item.setOfferId(ratingItem.getOfferId());

            if (request.getIncludeCrossOfferPromos()) {
                item.setQuantity(ratingItem.getQuantity());
                item.setShippingMethodId(ratingItem.getShippingMethodId());
            }

            items.add(item);
        }
    }

    @Override
    public RatingRequest buildResult() {
        RatingType type = super.getRatingType();

        RatingRequest result = new RatingRequest();
        result.setUserId(getUserId());
        result.setCountry(getCountry());
        result.setCurrency(getCurrency().getCurrencyCode());
        result.setTime(getTimestamp());

        //build offer level result
        result.setLineItems(new HashSet<RatingItem>());
        for (RatingResultEntry entry : getEntries()) {
            result.getLineItems().add(buildItem(entry, type));
        }

        if (RatingType.ORDER.equals(type)) {
            //build order level results
            RatingSummary ratingSummary = new RatingSummary();
            ratingSummary.setDiscountAmount(Utils.rounding(getOrderResult().getDiscountAmount(),
                    getCurrency().getNumberAfterDecimal()));
            ratingSummary.setPromotion(getOrderResult().getAppliedPromotion());

            BigDecimal finalTotalAmount = getOrderResult().getOriginalAmount().subtract(
                    getOrderResult().getDiscountAmount());
            ratingSummary.setFinalAmount(finalTotalAmount);

            result.setRatingSummary(ratingSummary);

            //build shipping fee calculation results
            ShippingSummary shippingSummary = new ShippingSummary();
            shippingSummary.setTotalShippingFee(getShippingResult().getShippingFee());
            shippingSummary.setPromotion(getShippingResult().getAppliedPromotion());
            result.setShippingMethodId(getDefaultShippingMethod());
            result.setShippingSummary(shippingSummary);
        }

        return result;
    }

    private RatingItem buildItem(RatingResultEntry entry, RatingType type) {
        RatingItem item = new RatingItem();
        item.setOfferId(entry.getOfferId());
        item.setQuantity(RatingType.OFFER.equals(type)? 1 : entry.getQuantity());
        item.setShippingMethodId(entry.getShippingMethodId());

        item.setPreOrderPrice(entry.getPreOrderPrice());
        item.setOriginalUnitPrice(entry.getOriginalPrice());
        item.setOriginalTotalPrice(entry.getOriginalPrice().multiply(new BigDecimal(item.getQuantity())));

        BigDecimal totalDiscount = entry.getDiscountAmount().multiply(new BigDecimal(item.getQuantity()));
        item.setTotalDiscountAmount(Utils.rounding(totalDiscount, getCurrency().getNumberAfterDecimal()));
        item.setPromotions(entry.getAppliedPromotion());

        item.setFinalTotalAmount(item.getOriginalTotalPrice().subtract(item.getTotalDiscountAmount()));
        if (RatingType.ORDER.equals(type)) {
            item.setDeveloperRevenue(Utils.rounding(item.getFinalTotalAmount().multiply(entry.getDeveloperRatio()),
                    getCurrency().getNumberAfterDecimal()));
        }

        return item;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Set<PromotionRevision>> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<String, Set<PromotionRevision>> candidates) {
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

    public String getDefaultShippingMethod() {
        return defaultShippingMethod;
    }

    public void setDefaultShippingMethod(String defaultShippingMethod) {
        this.defaultShippingMethod = defaultShippingMethod;
    }

    public ShippingResultEntry getShippingResult() {
        return shippingResult;
    }

    public void setShippingResult(ShippingResultEntry shippingResult) {
        this.shippingResult = shippingResult;
    }
}
