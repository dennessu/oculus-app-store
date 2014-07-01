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
    PENDING_CHARGE(1),
    PENDING_FULFILL(2),
    CHARGED(3),
    FULFILLED(4),
    COMPLETED(5),
    FAILED(6),
    CANCELED(7),
    REFUNDED(8),
    PREORDERED(9),
    PARTIAL_CHARGED(10),
    PRICE_RATING_CHANGED(11),
    RISK_REJECT(12),
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
