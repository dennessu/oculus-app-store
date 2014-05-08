/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by xmchen on 14-2-28.
 */
public enum TransactionType implements Identifiable<Short> {
    AUTHORIZE((short)1),
    CAPTURE((short)2),
    CHARGE((short)3),
    REVERSE((short)4),
    REFUND((short)5),
    CONFIRM((short)6),
    CHECK((short)7);

    private final Short id;

    TransactionType(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum TransactionType not settable");
    }
}
