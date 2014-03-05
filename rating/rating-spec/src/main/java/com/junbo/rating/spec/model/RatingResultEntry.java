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
    private Long offerId;
    private int quantity;
    private Money originalAmount;
    private Money discountAmount;
    private Set<Long> appliedPromotion;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Money getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Money originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Money getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Money discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Set<Long> getAppliedPromotion() {
        return appliedPromotion;
    }

    public void setAppliedPromotion(Set<Long> appliedPromotion) {
        this.appliedPromotion = appliedPromotion;
    }
}
