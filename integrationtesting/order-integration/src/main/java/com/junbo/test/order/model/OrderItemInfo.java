/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model;

import com.junbo.test.billing.entities.TaxInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiyu_000 on 6/26/14.
 */
public class OrderItemInfo {
    private String offerId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private BigDecimal totalTax;

    private BigDecimal totalDiscount;
    private List<TaxInfo> taxInfos;

    private List<FulfilmentHistory> fulfilmentHistories;

    public OrderItemInfo() {
        fulfilmentHistories = new ArrayList<>();
        taxInfos = new ArrayList<>();
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public List<TaxInfo> getTaxInfos() {
        return taxInfos;
    }

    public void setTaxInfos(List<TaxInfo> taxInfos) {
        this.taxInfos = taxInfos;
    }

    public List<FulfilmentHistory> getFulfilmentHistories() {
        return fulfilmentHistories;
    }

    public void setFulfilmentHistories(List<FulfilmentHistory> fulfilmentHistories) {
        this.fulfilmentHistories = fulfilmentHistories;
    }
}
