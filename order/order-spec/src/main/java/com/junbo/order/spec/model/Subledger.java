/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.*;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by chriszhu on 2/10/14.
 */
@JsonPropertyOrder(value = {
        "id", "subledgerType", "seller", "offer", "country", "currency", "item",
        "payoutStatus", "payoutId", "totalAmount", "totalPayoutAmount" , "totalQuantity", "startTime", "endTime", "resourceAge"
})
public class Subledger extends ResourceMetaForDualWrite<SubledgerId> {
    @JsonProperty("self")
    private SubledgerId id;
    @XSSFreeString
    private String subledgerType;
    private OrganizationId seller;
    private ItemId item;
    private OfferId offer; // default offer of the item
    @XSSFreeString
    private String payoutStatus;
    @JsonIgnore
    private String key;
    private Date startTime;
    private Date endTime;
    private CountryId country;
    private CurrencyId currency;
    private BigDecimal totalAmount;
    private BigDecimal totalPayoutAmount;
    private BigDecimal taxAmount;
    private Long totalQuantity;
    private PayoutId payoutId;
    @JsonIgnore
    private Map<String, String> properties;

    public SubledgerId getId() {
        return id;
    }

    public void setId(SubledgerId id) {
        this.id = id;
    }

    public String getSubledgerType() {
        return subledgerType;
    }

    public void setSubledgerType(String subledgerType) {
        this.subledgerType = subledgerType;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public OrganizationId getSeller() {
        return seller;
    }

    public void setSeller(OrganizationId seller) {
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public ItemId getItem() {
        return item;
    }

    public void setItem(ItemId item) {
        this.item = item;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalPayoutAmount() {
        return totalPayoutAmount;
    }

    public void setTotalPayoutAmount(BigDecimal totalPayoutAmount) {
        this.totalPayoutAmount = totalPayoutAmount;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public PayoutId getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(PayoutId payoutId) {
        this.payoutId = payoutId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
