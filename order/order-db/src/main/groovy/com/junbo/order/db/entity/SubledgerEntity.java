/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.db.entity.enums.SubledgerStatus;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

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
    private SubledgerStatus subledgerStatus;
    private Date startTime;
    private Date endTime;
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

    @Column(name = "STATUS_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public SubledgerStatus getSubledgerStatus() {
        return subledgerStatus;
    }

    public void setSubledgerStatus(SubledgerStatus subledgerStatus) {
        this.subledgerStatus = subledgerStatus;
    }

    @Column(name = "START_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "END_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
