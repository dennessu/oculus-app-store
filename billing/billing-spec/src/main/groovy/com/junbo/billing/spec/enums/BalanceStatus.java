/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.enums;

import com.junbo.common.util.Identifiable;

/**
 * Created by xmchen on 14-2-24.
 */
public enum BalanceStatus implements Identifiable<Short> {
    INIT((short)0),
    UNCONFIRMED((short)1),
    OPEN((short)2),
    PENDING_CAPTURE((short)3),
    PENDING_PAYMENT((short)4),
    CLOSED((short)5),

    FAILED((short)999),
    ERROR((short)-1);

    private final Short id;

    BalanceStatus(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }
}
