/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.mapper;

import java.math.BigDecimal;

/**
 * currency model.
 */
public class Currency {
    private Integer id;
    private String currencyCode;
    private BigDecimal minAuthAmount;
    private Integer baseUnit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getMinAuthAmount() {
        return minAuthAmount;
    }

    public void setMinAuthAmount(BigDecimal minAuthAmount) {
        this.minAuthAmount = minAuthAmount;
    }

    public Integer getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(Integer baseUnit) {
        this.baseUnit = baseUnit;
    }
}
