/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * charge info model.
 */
public class ChargeInfo {
    private String country;
    private String currency;
    private BigDecimal amount;
    private String businessDescriptor;
    private List<Item> items;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBusinessDescriptor() {
        return businessDescriptor;
    }

    public void setBusinessDescriptor(String businessDescriptor) {
        this.businessDescriptor = businessDescriptor;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
