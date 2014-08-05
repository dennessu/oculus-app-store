/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.enums;

/**
 * Created by Yunlong on 3/24/14.
 */
public enum Country {
    AE("AE", "United Arab Emirates", false, Currency.AED),
    AT("AT", "Austria", false, Currency.EUR),
    AU("AU", "Australia", false, Currency.AUD),
    BE("BE", "Belgium", false, Currency.EUR),
    BR("BR", "Brazil", true, Currency.BRL),
    CA("CA", "Canada", true, Currency.CAD),
    CH("CH", "Switzerland", false, Currency.CHF),
    CN("CN", "China", false, Currency.TWD),
    CY("CY", "Cyprus", false, Currency.EUR),
    CZ("CZ", "Czech Republic", false, Currency.EUR),
    DE("DE", "Germany", false, Currency.EUR),
    DK("DK", "Denmark", false, Currency.EUR),
    GR("GR", "Greece", false, Currency.EUR),
    ES("ES", "Spain", false, Currency.EUR),
    FI("FI", "Finland", false, Currency.EUR),
    FR("FR", "France", false, Currency.EUR),
    GB("GB", "United Kingdom", false, Currency.GBP),
    HK("HK", "Hong Kong", false, Currency.HKD),
    IE("IE", "Ireland", false, Currency.EUR),
    IN("IN", "India", false, Currency.INR),
    IT("IT", "Italy", false, Currency.EUR),
    JP("JP", "Japan", false, Currency.JPY),
    KR("KR", "Korea", false, Currency.KRW),
    MX("MX", "Mexico", false, Currency.MXN),
    NL("NL", "Netherlands", false, Currency.EUR),
    NO("NO", "Norway", false, Currency.NOK),
    NZ("NZ", "New Zealand", false, Currency.NZD),
    PL("PL", "Poland", false, Currency.PLN),
    PT("PT", "Portugal", false, Currency.PHP),
    RU("RU", "Russian Federation", false, Currency.RUB),
    SE("SE", "Sweden", false, Currency.SEK),
    SG("SG", "Singapore", false, Currency.SGD),
    SA("SA", "Saudi Arabia", false, Currency.SAR),
    TR("TR", "Turkey", false, Currency.TRY),
    TW("TW", "Taiwan", false, Currency.TWD),
    TH("TH", "Thailand", false, Currency.THB),
    UA("UA", "Ukraine", false, Currency.UAH),
    US("US", "United States", true, Currency.USD),
    ZA("ZA", "South Africa", false, Currency.ZAR),
    //define US as default country for common test
    DEFAULT("US", "United States", true, Currency.USD);


    private String countryCode;
    private String countryName;
    private boolean hasState;
    private Currency defaultCurrency;

    private Country(String countryCode, String countryName, boolean hasState, Currency defaultCurrency) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.hasState = hasState;
        this.defaultCurrency = defaultCurrency;
    }

    @Override
    public String toString() {
        return countryCode;
    }

}
