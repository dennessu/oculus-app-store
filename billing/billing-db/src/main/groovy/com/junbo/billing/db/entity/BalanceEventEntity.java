/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEventEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-24.
 */
@Entity
@Table(name = "balance_event")
public class BalanceEventEntity extends BaseEventEntity {
    @Column(name = "balance_id")
    private Long balanceId;

    @Column(name = "status_id")
    private Short statusId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    public Long getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    public Short getStatusId() {
        return statusId;
    }
    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
