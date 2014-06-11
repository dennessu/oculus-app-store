/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.enums;

/**
 * Created by weiyu_000 on 6/11/14.
 */
public enum  TransactionType {
    AUTHORIZE("AUTHORIZE"),
    CAPTURE("CAPTURE"),
    CHARGE("CHARGE"),
    REVERSE("REVERSE"),
    REFUND("REFUND"),
    CONFIRM("CONFIRM"),
    CHECK("CHECK");

    private final String type;

    TransactionType(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

}
