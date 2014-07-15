/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by minhao on 6/16/14.
 */
public enum PaymentAPI implements Identifiable<Short> {
    AddPI((short)1),
    RemovePI((short)2),
    UpdatePI((short)3),
    Auth((short)4),
    Capture((short)5),
    Confirm((short)6),
    Charge((short)7),
    Reverse((short)8),
    Refund((short)9),
    Credit((short)10),
    ReportEvent((short)11);

    private final Short id;

    PaymentAPI(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return this.id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum PaymentAPI not settable");
    }
}
