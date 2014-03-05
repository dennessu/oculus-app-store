/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.junbo.common.id.DiscountItemId;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-26.
 */
public class DiscountItem {
    private DiscountItemId discountItemId;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;

    public DiscountItemId getDiscountItemId() {
        return discountItemId;
    }

    public void setDiscountItemId(DiscountItemId discountItemId) {
        this.discountItemId = discountItemId;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }
}
