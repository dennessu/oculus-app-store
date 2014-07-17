/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by LinYi on 2/10/14.
 */
public enum OrderStatus implements Identifiable<Short> {
    OPEN(0),
    PENDING(1),
    COMPLETED(2),
    CANCELED(3),
    REFUNDED(4),
    PREORDERED(5),
    SHIPPED(6),
    DELIVERED(7),
    RETURNED(8),
    PRICE_RATING_CHANGED(100),
    RISK_REJECT(101),
    ERROR(-1);

    private OrderStatus(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum OrderStatus not settable");
    }


}
