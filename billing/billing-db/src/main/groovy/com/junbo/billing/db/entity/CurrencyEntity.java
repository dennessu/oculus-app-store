/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by xmchen on 14-2-13.
 */
@Entity
@Table(name = "currency")
public class CurrencyEntity implements Serializable {
    @Id
    @Column(name = "currency_name", nullable = false)
    private String name;

    @Column(name = "currency_code", nullable = false)
    private String numericCode;

    @Column(name = "base_units", nullable = false)
    private Integer baseUnits;

    @Column(name = "decimal_pattern", nullable = false)
    private String decimalPattern;

    @Column(name = "max_balance", nullable = false)
    private Double maxBalance;

    @Column(name = "spending_limit", nullable = false)
    private Double spendingLimit;

    @Column(name = "preauth_amount", nullable = false)
    private Double preauthAmount;

    @Column(name = "paypal_preauth_amount", nullable = false)
    private Double paypalPreauthAmount;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "orientation", nullable = false)
    private String orientation;

    @Column(name = "description")
    private String description;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNumericCode() {
        return numericCode;
    }
    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    public Integer getBaseUnits() {
        return baseUnits;
    }
    public void setBaseUnits(Integer baseUnits) {
        this.baseUnits = baseUnits;
    }

    public String getDecimalPattern() {
        return decimalPattern;
    }
    public void setDecimalPattern(String decimalPattern) {
        this.decimalPattern = decimalPattern;
    }

    public Double getMaxBalance() {
        return maxBalance;
    }
    public void setMaxBalance(Double maxBalance) {
        this.maxBalance = maxBalance;
    }

    public Double getSpendingLimit() {
        return spendingLimit;
    }
    public void setSpendingLimit(Double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public Double getPreauthAmount() {
        return preauthAmount;
    }
    public void setPreauthAmount(Double preauthAmount) {
        this.preauthAmount = preauthAmount;
    }

    public Double getPaypalPreauthAmount() {
        return paypalPreauthAmount;
    }
    public void setPaypalPreauthAmount(Double paypalPreauthAmount) {
        this.paypalPreauthAmount = paypalPreauthAmount;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getOrientation() {
        return orientation;
    }
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
