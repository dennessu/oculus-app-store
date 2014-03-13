/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.balance;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.EntityValidationCode;
import org.hibernate.validator.constraints.Length;

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
@Table(name = "balance_item")
public class BalanceItemEntity extends BaseEntity {
    private Long balanceItemId;
    private Long balanceId;
    private Long orderItemId;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private String financeId;
    private Boolean isTaxExempt;
    private Long originalBalanceItemId;

    @Id
    @Column(name = "balance_item_id")
    public Long getBalanceItemId() {
        return balanceItemId;
    }
    public void setBalanceItemId(Long balanceItemId) {
        this.balanceItemId = balanceItemId;
    }

    @Column(name = "balance_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    @Column(name = "order_item_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getOrderItemId() {
        return orderItemId;
    }
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "amount")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "tax_amount")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Column(name = "discount_amount")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Column(name = "finance_id")
    @Length(max=64, message= EntityValidationCode.TOO_LONG)
    public String getFinanceId() {
        return financeId;
    }
    public void setFinanceId(String financeId) {
        this.financeId = financeId;
    }

    @Column(name = "is_tax_exempt")
    public Boolean getIsTaxExempt() {
        return isTaxExempt;
    }
    public void setIsTaxExempt(Boolean isTaxExempt) {
        this.isTaxExempt = isTaxExempt;
    }

    @Column(name = "original_balance_item_id")
    public Long getOriginalBalanceItemId() {
        return originalBalanceItemId;
    }
    public void setOriginalBalanceItemId(Long originalBalanceItemId) {
        this.originalBalanceItemId = originalBalanceItemId;
    }
}
