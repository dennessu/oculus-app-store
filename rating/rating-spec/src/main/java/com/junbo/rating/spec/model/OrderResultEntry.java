/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model;

/**
 * Created by lizwu on 2/27/14.
 */
public class OrderResultEntry {
    private Money originalAmount;
    private Money discountAmount;
    private Long appliedPromotion;

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

    public Long getAppliedPromotion() {
        return appliedPromotion;
    }

    public void setAppliedPromotion(Long appliedPromotion) {
        this.appliedPromotion = appliedPromotion;
    }
}
