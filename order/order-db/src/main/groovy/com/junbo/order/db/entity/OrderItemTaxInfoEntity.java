/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 1/26/14.
 */
@Entity
@Table(name = "ORDER_ITEM_TAX_INFO")
public class OrderItemTaxInfoEntity extends CommonDbEntityWithDate {
    private Long orderItemTaxInfoId;
    private Long orderItemId;
    private BigDecimal totalTax;
    private Boolean isTaxInclusive;
    private Boolean isTaxExempt;
    private String taxCode;

    @Id
    @Column(name = "ORDER_ITEM_TAX_INFO_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemTaxInfoId() {
        return orderItemTaxInfoId;
    }

    public void setOrderItemTaxInfoId(Long orderItemTaxInfoId) {
        this.orderItemTaxInfoId = orderItemTaxInfoId;
    }

    @Column(name = "ORDER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "TOTAL_TAX")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    @Column(name = "IS_TAX_INCLUSIVE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getIsTaxInclusive() {
        return isTaxInclusive;
    }

    public void setIsTaxInclusive(Boolean isTaxInclusive) {
        this.isTaxInclusive = isTaxInclusive;
    }

    @Column(name = "IS_TAX_EXEMPT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getIsTaxExempt() {
        return isTaxExempt;
    }

    public void setIsTaxExempt(Boolean isTaxExempt) {
        this.isTaxExempt = isTaxExempt;
    }

    @Column(name = "TAX_CODE")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    @Override
    @Transient
    public Long getShardId() {
        return orderItemId;
    }
}
