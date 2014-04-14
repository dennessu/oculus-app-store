/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by xmchen on 14-1-26.
 */
public class Balance {

    @JsonProperty("self")
    private BalanceId balanceId;
    private UUID trackingUuid;
    private UserId userId;
    private OrderId orderId;
    private PaymentInstrumentId piId;
    private String type;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private Boolean taxIncluded;
    private String taxStatus;
    private String currency;
    private String country;
    private Date dueDate;
    private Boolean isAsyncCharge;
    private ShippingAddressId shippingAddressId;
    private BalanceId originalBalanceId;
    private Boolean skipTaxCalculation;

    private List<BalanceItem> balanceItems;

    private List<Transaction> transactions;

    public Balance() {
        balanceItems = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public BalanceId getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(BalanceId balanceId) {
        this.balanceId = balanceId;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }
    
    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public PaymentInstrumentId getPiId() {
        return piId;
    }

    public void setPiId(PaymentInstrumentId piId) {
        this.piId = piId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Boolean getTaxIncluded() {
        return taxIncluded;
    }

    public void setTaxIncluded(Boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<BalanceItem> getBalanceItems() {
        return balanceItems;
    }

    public void addBalanceItem(BalanceItem balanceItem) {
        balanceItems.add(balanceItem);
    }

    public BalanceId getOriginalBalanceId() {
        return originalBalanceId;
    }

    public void setOriginalBalanceId(BalanceId originalBalanceId) {
        this.originalBalanceId = originalBalanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ShippingAddressId getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(ShippingAddressId shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public String getTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(String taxStatus) {
        this.taxStatus = taxStatus;
    }

    public Boolean getIsAsyncCharge() {
        return isAsyncCharge;
    }

    public void setIsAsyncCharge(Boolean isAsyncCharge) {
        this.isAsyncCharge = isAsyncCharge;
    }

    public Boolean getSkipTaxCalculation() {
        return skipTaxCalculation;
    }

    public void setSkipTaxCalculation(Boolean skipTaxCalculation) {
        this.skipTaxCalculation = skipTaxCalculation;
    }
}
