/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.SubledgerId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chriszhu on 2/10/14.
 */
@JsonPropertyOrder(value = {
        "id", "seller", "offer", "country", "currency",
        "payoutStatus", "totalAmount", "startTime", "endTime", "resourceAge"
})
public class Subledger extends ResourceMeta implements Identifiable<SubledgerId> {
    @JsonProperty("self")
    private SubledgerId id;
    private UserId seller;
    private OfferId offer;
    private String payoutStatus;
    private Date startTime;
    private Date endTime;
    private CountryId country;
    private CurrencyId currency;
    private BigDecimal totalAmount;

    public SubledgerId getId() {
        return id;
    }

    public void setId(SubledgerId id) {
        this.id = id;
    }

    public UserId getSeller() {
        return seller;
    }

    public void setSeller(UserId seller) {
        this.seller = seller;
    }

    public OfferId getOffer() {
        return offer;
    }

    public void setOffer(OfferId offer) {
        this.offer = offer;
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

    public CountryId getCountry() {
        return country;
    }

    public void setCountry(CountryId country) {
        this.country = country;
    }

    public CurrencyId getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyId currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
