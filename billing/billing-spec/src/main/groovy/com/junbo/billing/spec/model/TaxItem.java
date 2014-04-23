/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-26.
 */
public class TaxItem {

    private String taxAuthority;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;
    private Boolean isTaxExempt;

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

    public Boolean getIsTaxExempt() {
        return isTaxExempt;
    }

    public void setIsTaxExempt(Boolean isTaxExempt) {
        this.isTaxExempt = isTaxExempt;
    }
}
