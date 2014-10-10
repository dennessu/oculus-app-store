/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

/**
 * ProviderOption.
 */
public class ProviderOption {
    private String country;
    private String currency;
    private String cesType;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCesType() {
        return cesType;
    }

    public void setCesType(String cesType) {
        this.cesType = cesType;
    }

}
