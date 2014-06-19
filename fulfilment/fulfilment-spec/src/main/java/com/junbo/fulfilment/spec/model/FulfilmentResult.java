/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * FulfilmentResult.
 */
public class FulfilmentResult {
    // entitlement fulfilment result
    private List<String> entitlementIds;

    // e-wallet fulfilment result
    private String currency;
    private BigDecimal amount;
    private Long transactionId;

    // physical goods fulfilment result
    // TBD

    public List<String> getEntitlementIds() {
        return entitlementIds;
    }

    public void setEntitlementIds(List<String> entitlementIds) {
        this.entitlementIds = entitlementIds;
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

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
