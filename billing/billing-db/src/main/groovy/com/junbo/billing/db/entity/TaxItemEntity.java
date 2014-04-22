/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "tax_item")
public class TaxItemEntity extends BaseEntity {
    @Id
    @Column(name = "tax_item_id")
    private Long taxItemId;

    @Column(name = "balance_item_id")
    private Long balanceItemId;

    @Column(name = "tax_authority_id")
    private Short taxAuthorityId;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "tax_rate")
    private BigDecimal taxRate;

    @Column(name = "deleted")
    private Boolean isDeleted = false;

    public Long getTaxItemId() {
        return taxItemId;
    }
    public void setTaxItemId(Long taxItemId) {
        this.taxItemId = taxItemId;
    }

    public Long getBalanceItemId() {
        return balanceItemId;
    }
    public void setBalanceItemId(Long balanceItemId) {
        this.balanceItemId = balanceItemId;
    }

    public Short getTaxAuthorityId() {
        return taxAuthorityId;
    }
    public void setTaxAuthorityId(Short taxAuthorityId) {
        this.taxAuthorityId = taxAuthorityId;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
