/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by xmchen on 14-4-17.
 */
public enum EventActionType implements Identifiable<Short> {
    CREATE((short)0),

    QUEUE((short)3),

    CHARGE((short)10),
    ASYNC_CHARGE((short)11),
    CAPTURE((short)12),
    ADDRESS_CHANGE((short)13),
    CONFIRM((short)14),
    CHECK((short)15),
    CHARGE_BACK((short)16),

    ADJUSTMENT((short)20);

    private final Short id;

    EventActionType(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum EventActionType not settable");
    }
}
