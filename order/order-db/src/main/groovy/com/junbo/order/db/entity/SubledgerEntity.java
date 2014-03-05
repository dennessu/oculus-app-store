/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 1/29/14.
 */

@Entity
@Table(name = "SUBLEDGER")
public class SubledgerEntity extends CommonDbEntityWithDate {
    private Long subledgerId;
    private Long sellerId;
    private Long sellerTaxProfileId;
    private Short currencyId;
    private BigDecimal totalAmount;
    private String property;

    @Id
    @Column(name = "SUBLEDGER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(Long subledgerId) {
        this.subledgerId = subledgerId;
    }

    @Column(name = "SELLER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    @Column(name = "SELLER_TAX_PROFILE_ID")
    public Long getSellerTaxProfileId() {
        return sellerTaxProfileId;
    }

    public void setSellerTaxProfileId(Long sellerTaxProfileId) {
        this.sellerTaxProfileId = sellerTaxProfileId;
    }

    @Column(name = "CURRENCY_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Short getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    @Column(name = "TOTAL_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "PROPERTY")
    @Length(max=4000, message=ValidationMessages.TOO_LONG)
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
