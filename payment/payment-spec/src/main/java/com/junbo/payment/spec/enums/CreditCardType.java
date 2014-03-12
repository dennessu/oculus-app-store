/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.enums;

/**
 * credit credit type enum.
 */
public enum CreditCardType {
    VISA("Visa"),
    AMEX("American Express"),
    CARTE_BLANCHE("Carte Blanche"),
    CHINA_UNION_PAY("China UnionPay"),
    DINERS_CLUB_INTERNATIONAL("Diners Club"),
    DISCOVER("Discover"),
    JCB("JCB"),
    LASER("Laser"),
    MAESTRO("Maestro"),
    MASTER_CARD("MasterCard"),
    SOLO("Solo"),
    SWITCH("Switch");

    private final String name;

    CreditCardType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
