/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.enums;

/**
 * Created by weiyu_000 on 6/11/14.
 */
public enum TransactionStatus {
    SUCCESS("SUCCESS"),
    DECLINE("DECLINE"),
    UNCONFIRMED("UNCONFIRMED"),

    TIMEOUT("TIMEOUT"),

    ERROR("ERROR");

    private final String status;

    TransactionStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

}
