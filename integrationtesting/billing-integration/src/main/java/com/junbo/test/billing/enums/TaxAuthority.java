/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.enums;

/**
 * Created by weiyu_000 on 6/26/14.
 */
public enum TaxAuthority {
    COUNTRY("COUNTRY"),
    STATE("STATE"),
    COUNTY("COUNTY"),
    CITY("CITY"),
    STATE2("STATE2"),
    COUNTY2("COUNTY2"),
    CITY2("CITY2"),
    DISTRICT("DISTRICT"),
    TERRITORY("TERRITORY"),
    PST("PST"),
    GST("GST"),
    VAT("VAT"),
    QST("QST"),
    HST("HST"),
    SPECIAL("SPECIAL"),
    ESTIMATE("ESTIMATE"),
    SERVICE("SERVICE"),
    EDUCESS("EDUCESS"),
    SHECESS("SHECESS"),
    IPI("IPI"),
    PIS("PIS"),
    COF("COF"),
    ICMS("ICMS"),
    ISS("ISS"),
    BT("BT"),
    SURCHARGE("SURCHARGE"),
    USWHT("USWHT"),
    BACKUPUSWHT("BACKUPUSWHT"),
    IST("IST"),

    UNKNOWN("UNKNOWN");

    private final String type;


   TaxAuthority(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

}
