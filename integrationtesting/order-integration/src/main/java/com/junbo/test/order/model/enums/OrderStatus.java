/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for order order status.
 *
 * @author Yunlongzhao
 *         Created on 6/5/2014.
 */

public enum OrderStatus {
    OPEN("OPEN", 0),
    PENDING_CHARGE("PENDING_CHARGE", 1),
    PENDING_FULFILL("PENDING_FULFILL", 2),
    CHARGED("CHARGED", 3),
    FULFILLED("FULFILLED", 4),
    COMPLETED("COMPLETED", 5),
    FAILED("FAILED", 6),
    CANCELED("CANCELED", 7),
    REFUNDED("REFUNDED", 8),
    PREORDERED("PREORDERED", 9),
    PARTIAL_CHARGED("PARTIAL_CHARGED", 10),
    PRICE_RATING_CHANGED("PRICE_RATING_CHANGED", 11),
    ERROR("ERROR", -1);

    private String name;
    private int status;

    private OrderStatus(String name, int status) {
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
