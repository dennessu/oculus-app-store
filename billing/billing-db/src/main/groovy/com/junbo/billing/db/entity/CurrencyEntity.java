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
    private String name;
    private String numericCode;
    private Integer baseUnits;
    private String decimalPattern;
    private Double maxBalance;
    private Double spendingLimit;
    private Double preauthAmount;
    private Double paypalPreauthAmount;
    private String symbol;
    private String orientation;
    private String description;

    @Id
    @Column(name = "currency_name", nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "currency_code", nullable = false)
    public String getNumericCode() {
        return numericCode;
    }
    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    @Column(name = "base_units", nullable = false)
    public Integer getBaseUnits() {
        return baseUnits;
    }
    public void setBaseUnits(Integer baseUnits) {
        this.baseUnits = baseUnits;
    }

    @Column(name = "decimal_pattern", nullable = false)
    public String getDecimalPattern() {
        return decimalPattern;
    }
    public void setDecimalPattern(String decimalPattern) {
        this.decimalPattern = decimalPattern;
    }

    @Column(name = "max_balance", nullable = false)
    public Double getMaxBalance() {
        return maxBalance;
    }
    public void setMaxBalance(Double maxBalance) {
        this.maxBalance = maxBalance;
    }

    @Column(name = "spending_limit", nullable = false)
    public Double getSpendingLimit() {
        return spendingLimit;
    }
    public void setSpendingLimit(Double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    @Column(name = "preauth_amount", nullable = false)
    public Double getPreauthAmount() {
        return preauthAmount;
    }
    public void setPreauthAmount(Double preauthAmount) {
        this.preauthAmount = preauthAmount;
    }

    @Column(name = "paypal_preauth_amount", nullable = false)
    public Double getPaypalPreauthAmount() {
        return paypalPreauthAmount;
    }
    public void setPaypalPreauthAmount(Double paypalPreauthAmount) {
        this.paypalPreauthAmount = paypalPreauthAmount;
    }

    @Column(name = "symbol", nullable = false)
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Column(name = "orientation", nullable = false)
    public String getOrientation() {
        return orientation;
    }
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
