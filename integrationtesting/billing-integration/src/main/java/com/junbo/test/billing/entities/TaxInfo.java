/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.entities;

import com.junbo.billing.spec.enums.TaxAuthority;

import java.math.BigDecimal;

/**
 * Created by weiyu_000 on 6/26/14.
 */
public class TaxInfo {
    private TaxAuthority taxType;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;
    private boolean isTaxExempted;

    public TaxAuthority getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxAuthority taxType) {
        this.taxType = taxType;
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

    public boolean isTaxExempted() {
        return isTaxExempted;
    }

    public void setTaxExempted(boolean isTaxExempted) {
        this.isTaxExempted = isTaxExempted;
    }
}
