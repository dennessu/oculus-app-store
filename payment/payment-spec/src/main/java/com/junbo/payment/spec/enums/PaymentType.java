/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * payment type enum.
 */
public enum PaymentType implements Identifiable<Short> {
    AUTHORIZE((short)1),
    CREDIT((short)2),
    CHARGE((short)3);

    private final Short id;

    PaymentType(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return this.id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum PaymentStatus not settable");
    }
}
