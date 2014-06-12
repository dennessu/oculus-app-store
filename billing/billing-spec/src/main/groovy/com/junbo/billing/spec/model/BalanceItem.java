/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.billing.spec.enums.PropertyKey;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xmchen on 14-1-26.
 */
public class BalanceItem extends ResourceMetaForDualWrite<Long> {
    private Long id;
    @JsonIgnore
    private Long balanceId;
    private OrderId orderId;
    private OrderItemId orderItemId;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private String financeId;
    private Long originalBalanceItemId;
    private Map<String, String> propertySet;

    private List<TaxItem> taxItems;
    private List<DiscountItem> discountItems;

    public BalanceItem() {
        taxItems = new ArrayList<>();
        discountItems = new ArrayList<>();
        propertySet = new HashMap<>();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getFinanceId() {
        return financeId;
    }

    public void setFinanceId(String financeId) {
        this.financeId = financeId;
    }

    public List<TaxItem> getTaxItems() {
        return taxItems;
    }

    public void addTaxItem(TaxItem taxItem) {
        taxItems.add(taxItem);
    }

    public List<DiscountItem> getDiscountItems() {
        return discountItems;
    }

    public void addDiscountItem(DiscountItem discountItem) {
        discountItems.add(discountItem);
    }

    public Long getOriginalBalanceItemId() {
        return originalBalanceItemId;
    }

    public void setOriginalBalanceItemId(Long originalBalanceItemId) {
        this.originalBalanceItemId = originalBalanceItemId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
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

    public void addPropertySet(PropertyKey key, String value) {
        propertySet.put(key.name(), value);
    }
}
