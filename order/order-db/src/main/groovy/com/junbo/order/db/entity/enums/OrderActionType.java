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
public enum OrderActionType implements Identifiable<Short> {
    RATE(0),
    CHARGE(1),
    AUTHORIZE(2),
    FULFILL(3),
    REFUND(4),
    CANCEL(5),
    UPDATE(6),
    PREORDER(7),
    PARTIAL_REFUND(8);

    private OrderActionType(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }
}
