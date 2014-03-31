/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.EntityValidationCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "discount_item")
public class DiscountItemEntity extends BaseEntity {
    private Long discountItemId;
    private Long balanceItemId;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;

    @Id
    @Column(name = "discount_item_id")
    public Long getDiscountItemId() {
        return discountItemId;
    }
    public void setDiscountItemId(Long discountItemId) {
        this.discountItemId = discountItemId;
    }

    @Column(name = "balance_item_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getBalanceItemId() {
        return balanceItemId;
    }
    public void setBalanceItemId(Long balanceItemId) {
        this.balanceItemId = balanceItemId;
    }

    @Column(name = "discount_amount")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Column(name = "discount_rate")
    public BigDecimal getDiscountRate() {
        return discountRate;
    }
    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

}
