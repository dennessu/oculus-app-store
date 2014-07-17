/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model;

/**
 * Created by lizwu on 7/17/14.
 */
public class Currency {
    private String currencyCode;
    private int numberAfterDecimal;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getNumberAfterDecimal() {
        return numberAfterDecimal;
    }

    public void setNumberAfterDecimal(int numberAfterDecimal) {
        this.numberAfterDecimal = numberAfterDecimal;
    }
}
