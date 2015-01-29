/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.billing.spec.enums.PropertyKey;
import com.junbo.common.id.*;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xmchen on 14-1-26.
 */
public class Balance extends ResourceMetaForDualWrite<BalanceId> {
    @JsonProperty("self")
    private BalanceId id;

    private UUID trackingUuid;
    private UserId userId;
    private List<OrderId> orderIds;
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
    private UserPersonalInfoId shippingAddressId;
    private BalanceId originalBalanceId;
    private Boolean skipTaxCalculation;

    private String successRedirectUrl;
    private String cancelRedirectUrl;
    private String providerConfirmUrl;

    @JsonIgnore
    private String requestorId;

    private Map<String, String> propertySet;

    private List<BalanceItem> balanceItems;
    private List<Transaction> transactions;

    public Balance() {
        balanceItems = new ArrayList<>();
        transactions = new ArrayList<>();
        propertySet = new HashMap<>();
    }

    @Override
    public BalanceId getId() {
        return id;
    }

    @Override
    public void setId(BalanceId id) {
        this.id = id;
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

    public List<OrderId> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<OrderId> orderIds) {
        this.orderIds = orderIds;
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

    public String getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public BalanceItem getBalanceItem(Long balanceItemId) {
        for (BalanceItem item : balanceItems) {
            if (item.getId().equals(balanceItemId)) {
                return item;
            }
        }
        return null;
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

    public UserPersonalInfoId getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(UserPersonalInfoId shippingAddressId) {
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

    public String getSuccessRedirectUrl() {
        return successRedirectUrl;
    }

    public void setSuccessRedirectUrl(String successRedirectUrl) {
        this.successRedirectUrl = successRedirectUrl;
    }

    public String getCancelRedirectUrl() {
        return cancelRedirectUrl;
    }

    public void setCancelRedirectUrl(String cancelRedirectUrl) {
        this.cancelRedirectUrl = cancelRedirectUrl;
    }

    public String getProviderConfirmUrl() {
        return providerConfirmUrl;
    }

    public void setProviderConfirmUrl(String providerConfirmUrl) {
        this.providerConfirmUrl = providerConfirmUrl;
    }

    public Map<String, String> getPropertySet() {
        return propertySet;
    }

    public void setPropertySet(Map<String, String> propertySet) {
        if (propertySet == null) {
            return;
        }
        this.propertySet = propertySet;
    }

    public void addProperty(PropertyKey key, String value) {
        propertySet.put(key.name(), value);
    }

    public String getProperty(PropertyKey key) {
        if (propertySet.containsKey(key.name())) {
            return propertySet.get(key.name());
        }
        return null;
    }
}
