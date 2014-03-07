/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model;

import java.math.BigDecimal;

/**
 * Created by lizwu on 3/7/14.
 */
public class ShippingResultEntry {
    private BigDecimal shippingFee;
    private Long appliedPromotion;

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Long getAppliedPromotion() {
        return appliedPromotion;
    }

    public void setAppliedPromotion(Long appliedPromotion) {
        this.appliedPromotion = appliedPromotion;
    }
}
