/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.SellerId;
import com.junbo.common.id.SellerTaxProfileId;
import com.junbo.common.id.SubledgerId;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chriszhu on 2/10/14.
 */
public class Subledger extends BaseModelWithDate {
    @JsonProperty("self")
    private SubledgerId subledgerId;
    private SellerId sellerId;
    private SellerTaxProfileId sellerTaxProfileId;
    private PayoutStatus payoutStatus;
    private Date payoutTime;
    private String country;
    private String currency;
    private BigDecimal payoutAmount;
    private Integer resourceAge;

    public SubledgerId getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(SubledgerId subledgerId) {
        this.subledgerId = subledgerId;
    }

    public SellerId getSellerId() {
        return sellerId;
    }

    public void setSellerId(SellerId sellerId) {
        this.sellerId = sellerId;
    }

    public SellerTaxProfileId getSellerTaxProfileId() {
        return sellerTaxProfileId;
    }

    public void setSellerTaxProfileId(SellerTaxProfileId sellerTaxProfileId) {
        this.sellerTaxProfileId = sellerTaxProfileId;
    }

    public PayoutStatus getPayoutStatus() {
        return payoutStatus;
    }

    public void setPayoutStatus(PayoutStatus payoutStatus) {
        this.payoutStatus = payoutStatus;
    }

    public Date getPayoutTime() {
        return payoutTime;
    }

    public void setPayoutTime(Date payoutTime) {
        this.payoutTime = payoutTime;
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

    public BigDecimal getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(BigDecimal payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }
}
