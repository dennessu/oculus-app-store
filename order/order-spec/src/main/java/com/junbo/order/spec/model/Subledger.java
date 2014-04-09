/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.SellerTaxProfileId;
import com.junbo.common.id.SubledgerId;
import com.junbo.common.id.UserId;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chriszhu on 2/10/14.
 */
public class Subledger extends BaseModelWithDate {
    @JsonProperty("self")
    private SubledgerId subledgerId;
    private UserId sellerId;
    private SellerTaxProfileId sellerTaxProfileId;
    private OfferId offerId;
    private String payoutStatus;
    private Date startTime;
    private Date endTime;
    private String country;
    private String currency;
    private BigDecimal totalAmount;
    private Integer resourceAge;

    public SubledgerId getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(SubledgerId subledgerId) {
        this.subledgerId = subledgerId;
    }

    public UserId getSellerId() {
        return sellerId;
    }

    public void setSellerId(UserId sellerId) {
        this.sellerId = sellerId;
    }

    public SellerTaxProfileId getSellerTaxProfileId() {
        return sellerTaxProfileId;
    }

    public void setSellerTaxProfileId(SellerTaxProfileId sellerTaxProfileId) {
        this.sellerTaxProfileId = sellerTaxProfileId;
    }

    public OfferId getOfferId() {
        return offerId;
    }

    public void setOfferId(OfferId offerId) {
        this.offerId = offerId;
    }

    public String getPayoutStatus() {
        return payoutStatus;
    }

    public void setPayoutStatus(String payoutStatus) {
        this.payoutStatus = payoutStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getRev() {
        return resourceAge;
    }

    public void setRev(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }
}
