/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-26.
 */
public class DiscountItem extends ResourceMetaForDualWrite<Long> {
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private Long balanceItemId;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Long getBalanceItemId() {
        return balanceItemId;
    }

    public void setBalanceItemId(Long balanceItemId) {
        this.balanceItemId = balanceItemId;
    }
}
