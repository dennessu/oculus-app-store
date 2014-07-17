/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for billing action.
 *
 * @author Yunlong
 *         Created on 6/5/2014.
 */
public enum BillingAction {
    CHARGE("CHARGE", 0),
    AUTHORIZE("AUTHORIZE", 1),
    CREDIT("CREDIT", 2),
    REFUND("REFUND", 3),
    CAPTURE("CAPTURE", 4),
    DEPOSIT("DEPOSIT", 5),
    CANCEL("CANCEL", 6),
    REQUEST_CHARGE(" REQUEST_CHARGE", 100),
    REQUEST_CREDIT("REQUEST_CREDIT", 102),
    REQUEST_REFUND("REQUEST_REFUND", 103),
    REQUEST_DEPOSIT("REQUEST_DEPOSIT", 105),
    REQUEST_CANCEL("REQUEST_CANCEL", 106);

    private String name;
    private int status;

    private BillingAction(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return name;
    }

}
