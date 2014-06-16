/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * payment event type enum.
 */
public enum PaymentEventType implements Identifiable<Short> {
    AUTH_CREATE((short)1),
    AUTHORIZE((short)2),
    SUBMIT_SETTLE_CREATE((short)3),
    SUBMIT_SETTLE((short)4),
    REVERSE_CREATE((short)5),
    AUTH_REVERSE((short)6),
    SUBMIT_SETTLE_REVERSE((short)7),
    IMMEDIATE_SETTLE_CREATE((short)8),
    IMMEDIATE_SETTLE((short)9),
    REFUND_CREATE((short)10),
    REFUND((short)11),
    REPORT_EVENT((short)12),
    CREDIT_CREATE((short)13),
    CREDIT((short)14),
    NOTIFY((short)15);

    private final Short id;

    PaymentEventType(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return this.id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum PaymentEventType not settable");
    }
}
