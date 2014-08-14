/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.enums;

/**
 * Created by Yunlong on 3/24/14.
 */
public enum Currency {
    AED("AED", "AED"),
    ARS("ARS", "$"),
    AUD("AUD", "$"),

    BGN("BGN", "BGN"),
    BHD("BHD", "BHD"),
    BRL("BRL", "R$"),

    CAD("CAD", "$"),
    CHF("CHF", "CHF"),
    CLP("CLP", "$"),
    COP("COP", "$"),
    CNY("CNY", "￥"),
    CZK("CZK", "Kč"),

    DKK("DKK", "kr"),
    DOP("DOP", "RD$"),

    EEK("EEK", "EEK"),
    EGP("EGP", "£"),
    EUR("EUR", "€"),

    GBP("GBP", "£"),
    GTQ("GTQ", "Q"),

    HKD("HKD", "HK$"),
    HUF("HUF", "Ft"),

    IDR("IDR", "Rp"),
    ILS("ILS", "ILS"),
    INR("INR", "₹"),

    JOD("JOD", "JOD"),
    JPY("JPY", "¥"),

    KRW("KRW", "₩"),

    LTL("LTL", "LTL"),
    LVL("LVL", "LVL"),

    MXN("MXN", "$"),
    MYR("MYR", "RM"),

    NOK("NOK", "kr"),
    NZD("NZD", "$"),

    OMR("OMR", "OMR"),

    PEN("PEN", "S/."),
    PHP("PHP", "Php"),
    PLN("PLN", "zł"),

    QAR("QAR", "QAR"),

    RRD("RRD", "RRD"),
    RUB("RUB", "руб"),

    SAR("SAR", "﷼"),
    SVC("SVC", "SVC"),
    SEK("SEK", "kr"),
    SGD("SGD", "S$"),

    THB("THB", "฿"),
    TRY("TRY", "TL"),
    TWD("TWD", "NT$"),

    UAH("UAH", "₴"),
    USD("USD", "$"),

    VEF("VEF", "Bs.F"),
    VND("VND", "₫"),

    YER("YER", "YER"),
    ZAR("ZAR", "R"),

    FREE("XXX",""),

    //define USD as default currency for common test
    DEFAULT("USD", "$");

    private String currencyCode;
    private String currencySymbol;

    private Currency(String currencyCode, String currencySymbol) {
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
    }

    @Override
    public String toString() {
        return currencyCode;
    }

}
