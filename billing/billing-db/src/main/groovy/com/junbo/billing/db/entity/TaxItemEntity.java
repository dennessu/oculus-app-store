/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.EntityValidationCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "tax_item")
public class TaxItemEntity extends BaseEntity {
    private Long taxItemId;
    private Long balanceItemId;
    private Short taxAuthorityId;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;

    @Id
    @Column(name = "tax_item_id")
    public Long getTaxItemId() {
        return taxItemId;
    }
    public void setTaxItemId(Long taxItemId) {
        this.taxItemId = taxItemId;
    }

    @Column(name = "balance_item_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getBalanceItemId() {
        return balanceItemId;
    }
    public void setBalanceItemId(Long balanceItemId) {
        this.balanceItemId = balanceItemId;
    }

    @Column(name = "tax_authority_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Short getTaxAuthorityId() {
        return taxAuthorityId;
    }
    public void setTaxAuthorityId(Short taxAuthorityId) {
        this.taxAuthorityId = taxAuthorityId;
    }

    @Column(name = "tax_amount")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Column(name = "tax_rate")
    public BigDecimal getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
}
