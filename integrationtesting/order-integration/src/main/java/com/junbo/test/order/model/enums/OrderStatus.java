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
    PENDING("PENDING", 1),
    COMPLETED("COMPLETED", 2),
    CANCELED("CANCELED", 3),
    REFUNDED("REFUNDED", 4),
    PREORDERED("PREORDERED", 5),
    SHIPPED("SHIPPED",6),
    DELIVERED("DELIVERED",7),
    RETURNED("RETURNED",8),
    PRICE_RATING_CHANGED("PRICE_RATING_CHANGED",100),
    RISK_REJECT("RISK_REJECT",101),
    AUDITED("AUDITED",102),
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
