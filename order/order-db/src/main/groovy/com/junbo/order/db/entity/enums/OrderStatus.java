/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity.enums;

import com.junbo.common.util.Identifiable;

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
    ERROR(-1);

    private OrderStatus(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }
}
