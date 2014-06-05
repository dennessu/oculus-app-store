/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for payout status.
 *
 * @author Yunlong
 *         Created on 6/5/2014.
 */

public enum PayoutStatus{
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private String name;

    private PayoutStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
