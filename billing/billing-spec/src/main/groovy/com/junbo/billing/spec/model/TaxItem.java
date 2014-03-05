/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.junbo.common.id.TaxItemId;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-26.
 */
public class TaxItem {

    private TaxItemId taxItemId;
    private String taxAuthority;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;

    public TaxItemId getTaxItemId() {
        return taxItemId;
    }

    public void setTaxItemId(TaxItemId taxItemId) {
        this.taxItemId = taxItemId;
    }

    public String getTaxAuthority() {
        return taxAuthority;
    }

    public void setTaxAuthority(String taxAuthority) {
        this.taxAuthority = taxAuthority;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
}
