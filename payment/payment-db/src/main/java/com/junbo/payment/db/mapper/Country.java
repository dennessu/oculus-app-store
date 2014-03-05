/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.mapper;


/**
 * country model.
 */
public class Country {
    private Integer id;
    private String countryCode;
    private String country3Code;
    private String countryName;
    private Integer defaultCurrencyId;

    public Integer getDefaultCurrencyId() {
        return defaultCurrencyId;
    }

    public void setDefaultCurrencyId(Integer defaultCurrencyId) {
        this.defaultCurrencyId = defaultCurrencyId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry3Code() {
        return country3Code;
    }

    public void setCountry3Code(String country3Code) {
        this.country3Code = country3Code;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
