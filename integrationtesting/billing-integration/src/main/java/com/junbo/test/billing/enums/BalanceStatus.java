/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.enums;

/**
 * Created by Yunlong on 4/9/14.
 */
public enum  BalanceStatus {
    INIT("INIT"),
    UNCONFIRMED("UNCONFIRMED"),

    PENDING_CAPTURE("PENDING_CAPTURE"),

    AWAITING_PAYMENT("AWAITING_PAYMENT"),
    COMPLETED("COMPLETED"),

    FAILED("FAILED"),
    CANCELLED("CANCELLED"),
    ERROR("ERROR");

    private final String status;

    BalanceStatus(String balanceStatus){
         this.status = balanceStatus;
    }

    @Override
    public String toString() {
        return status;
    }

}
