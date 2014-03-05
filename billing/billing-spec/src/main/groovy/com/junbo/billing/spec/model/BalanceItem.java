/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.junbo.common.id.BalanceItemId;
import com.junbo.common.id.OrderItemId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
public class BalanceItem {
    private BalanceItemId balanceItemId;
    private OrderItemId orderItemId;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private Boolean taxIncluded;
    private String financeId;
    private Boolean isTaxExempt;
    private BalanceItemId originalBalanceItemId;

    private List<TaxItem> taxItems;
    private List<DiscountItem> discountItems;

    public BalanceItem() {
        taxItems = new ArrayList<TaxItem>();
        discountItems = new ArrayList<DiscountItem>();
    }

    public BalanceItemId getBalanceItemId() {
        return balanceItemId;
    }

    public void setBalanceItemId(BalanceItemId balanceItemId) {
        this.balanceItemId = balanceItemId;
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

    public Boolean getTaxIncluded() {
        return taxIncluded;
    }

    public void setTaxIncluded(Boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
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

    public Boolean getIsTaxExempt() {
        return isTaxExempt;
    }

    public void setIsTaxExempt(Boolean isTaxExempt) {
        this.isTaxExempt = isTaxExempt;
    }

    public BalanceItemId getOriginalBalanceItemId() {
        return originalBalanceItemId;
    }

    public void setOriginalBalanceItemId(BalanceItemId originalBalanceItemId) {
        this.originalBalanceItemId = originalBalanceItemId;
    }
}
