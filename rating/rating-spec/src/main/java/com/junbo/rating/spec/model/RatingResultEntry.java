/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model;

import java.util.Set;

/**
 * Created by lizwu on 2/25/14.
 */
public class RatingResultEntry {
    private String offerId;
    private int quantity;
    private String shippingMethodId;
    private Money preOrderPrice;
    private Money originalPrice;
    private Money discountAmount;
    private Set<String> appliedPromotion;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(String shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public Money getPreOrderPrice() {
        return preOrderPrice;
    }

    public void setPreOrderPrice(Money preOrderPrice) {
        this.preOrderPrice = preOrderPrice;
    }

    public Money getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Money originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Money getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Money discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Set<String> getAppliedPromotion() {
        return appliedPromotion;
    }

    public void setAppliedPromotion(Set<String> appliedPromotion) {
        this.appliedPromotion = appliedPromotion;
    }
}
