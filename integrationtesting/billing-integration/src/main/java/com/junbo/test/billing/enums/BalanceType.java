/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.enums;

/**
 * Created by Yunlong on 4/9/14.
 */
public enum BalanceType {

    DEBIT("DEBIT"),
    DELAY_DEBIT("DELAY_DEBIT"),
    MANUAL_CAPTURE("MANUAL_CAPTURE"),
    CREDIT("CREDIT"),
    REFUND("REFUND");

    private final String type;

    BalanceType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
