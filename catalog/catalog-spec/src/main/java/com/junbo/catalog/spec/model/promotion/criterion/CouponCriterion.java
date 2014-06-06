/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion.criterion;

/**
 * Coupon criterion.
 */
public class CouponCriterion extends Criterion {
    private String couponClass;

    public String getCouponClass() {
        return couponClass;
    }

    public void setCouponClass(String couponClass) {
        this.couponClass = couponClass;
    }
}
