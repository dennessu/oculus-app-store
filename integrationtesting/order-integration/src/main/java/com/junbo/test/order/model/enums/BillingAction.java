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
    CHARGE("CHARGE"),
    AUTHORIZE("AUTHORIZE"),
    CREDIT("CREDIT"),
    REFUND("REFUND"),
    CAPTURE("CAPTURE"),
    DEPOSIT("DEPOSIT"),
    PENDING_CHARGE("PENDING_CHARGE");

    private String name;

    private BillingAction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
