/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "discount_item")
public class DiscountItemEntity extends BaseEntity {
    @Id
    @Column(name = "discount_item_id")
    private Long discountItemId;

    @Column(name = "balance_item_id")
    private Long balanceItemId;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    public Long getDiscountItemId() {
        return discountItemId;
    }
    public void setDiscountItemId(Long discountItemId) {
        this.discountItemId = discountItemId;
    }

    public Long getBalanceItemId() {
        return balanceItemId;
    }
    public void setBalanceItemId(Long balanceItemId) {
        this.balanceItemId = balanceItemId;
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
