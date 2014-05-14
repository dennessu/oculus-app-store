/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by chriszhu on 2/25/14.
 */
public enum BillingAction implements Identifiable<Short> {
    CHARGE(0),
    AUTHORIZE(1),
    CREDIT(2),
    REFUND(3),
    CAPTURE(4),
    DEPOSIT(5),
    PENDING_CHARGE(6);

    private BillingAction(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum DiscountType not settable");
    }

}
