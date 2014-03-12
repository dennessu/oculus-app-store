/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model;

import com.junbo.rating.spec.error.AppErrors;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by lizwu on 2/20/14.
 */
public class Money {
    public static final Money NOT_FOUND = null;
    public static final RoundingMode MODE = RoundingMode.DOWN;

    private BigDecimal value;
    private String currency;

    public Money() {
    }

    public Money(String currency) {
        this.currency = currency;
    }

    public Money(BigDecimal value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equalsIgnoreCase(other.getCurrency())){
            AppErrors.INSTANCE.currencyNotConsistent(this.currency, other.getCurrency());
        }

        return new Money(this.value.add(other.getValue()), this.currency);
    }

    public Money subtract(Money other) {
        if (!this.currency.equalsIgnoreCase(other.getCurrency())){
            AppErrors.INSTANCE.currencyNotConsistent(this.currency, other.getCurrency());
        }

        return new Money(this.value.subtract(other.getValue()), this.currency);
    }

    public Money multiple(int quantity) {
        return new Money(this.value.multiply(new BigDecimal(quantity)), currency);
    }

    public boolean greaterThan(Money other) {
        if (!this.currency.equalsIgnoreCase(other.getCurrency())){
            AppErrors.INSTANCE.currencyNotConsistent(this.currency, other.getCurrency());
        }

        return this.value.compareTo(other.getValue()) > 0;
    }
}
