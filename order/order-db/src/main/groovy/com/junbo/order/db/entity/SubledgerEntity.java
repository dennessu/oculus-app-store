/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.PayoutStatus;
import com.junbo.order.spec.model.enums.SubledgerType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

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
    private SubledgerType subledgerType;
    private Long sellerId;
    private String offerId;
    private String itemId;
    private Long sellerTaxProfileId;
    private Long payoutId;
    private String key ;
    private String currency;
    private String country;
    private BigDecimal totalAmount;
    private BigDecimal totalPayoutAmount;
    private BigDecimal taxAmount;
    private Long totalQuantity;
    private PayoutStatus payoutStatus;
    private Date startTime;
    private Date endTime;
    private String properties;

    @Id
    @Column(name = "SUBLEDGER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(Long subledgerId) {
        this.subledgerId = subledgerId;
    }

    @Column (name = "SUBLEDGER_TYPE")
    @NotNull (message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.SubledgerEnumType")
    public SubledgerType getSubledgerType() {
        return subledgerType;
    }

    public void setSubledgerType(SubledgerType subledgerType) {
        this.subledgerType = subledgerType;
    }

    @Column(name = "SELLER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    @Column(name = "OFFER_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    @Column(name = "ITEM_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    @Column(name = "SUBLEDGER_KEY")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Column(name = "PAYOUT_ID")
    public Long getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(Long payoutId) {
        this.payoutId = payoutId;
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

    @Column(name = "TOTAL_PAYOUT_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalPayoutAmount() {
        return totalPayoutAmount;
    }

    public void setTotalPayoutAmount(BigDecimal totalPayoutAmount) {
        this.totalPayoutAmount = totalPayoutAmount;
    }

    @Column(name = "TAX_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Column(name = "TOTAL_QUANTITY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
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

    @Column(name = "properties")
    @Length(max=4000, message=ValidationMessages.TOO_LONG)
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    @Transient
    public Long getShardId() {
        return subledgerId;
    }
}
