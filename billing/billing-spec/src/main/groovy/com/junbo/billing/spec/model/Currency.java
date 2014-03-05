/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

/**
 * Created by xmchen on 14-2-13.
 */
public class Currency {
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
