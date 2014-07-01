/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.enums;

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
    REQUEST_CHARGE(100),
    REQUEST_AUTHORIZE(101),
    REQUEST_CREDIT(102),
    REQUEST_REFUND(103),
    REQUEST_CAPTURE(104),
    REQUEST_DEPOSIT(105);

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
        throw new NotSupportedException("enum BillingAction not settable");
    }

}
