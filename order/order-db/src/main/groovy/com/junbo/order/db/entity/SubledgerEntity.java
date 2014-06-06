/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.PayoutStatus;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
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
    private String productItemId;
    private Long sellerTaxProfileId;
    private String currency;
    private String country;
    private BigDecimal totalAmount;
    private PayoutStatus payoutStatus;
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

    @Column(name = "PRODUCT_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    @Column(name = "SELLER_TAX_PROFILE_ID")
    public Long getSellerTaxProfileId() {
        return sellerTaxProfileId;
    }

    public void setSellerTaxProfileId(Long sellerTaxProfileId) {
        this.sellerTaxProfileId = sellerTaxProfileId;
    }

    @Column(name = "CURRENCY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "COUNTRY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "TOTAL_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "PAYOUT_STATUS_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.SubledgerPayoutStatusType")
    public PayoutStatus getPayoutStatus() {
        return payoutStatus;
    }

    public void setPayoutStatus(PayoutStatus payoutStatus) {
        this.payoutStatus = payoutStatus;
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

    @Override
    @Transient
    public Long getShardId() {
        return subledgerId;
    }
}
