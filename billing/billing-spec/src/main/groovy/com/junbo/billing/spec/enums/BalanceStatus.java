/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by xmchen on 14-2-24.
 */
public enum BalanceStatus implements Identifiable<Short> {
    INIT((short)0),
    QUEUING((short)1),

    UNCONFIRMED((short)10),
    PENDING_RISK_REVIEW((short)30),
    PENDING_CAPTURE((short)50),

    AWAITING_PAYMENT((short)80),
    COMPLETED((short)100),

    CHARGE_BACK((short)900),
    FAILED((short)999),
    CANCELLED((short)1000),
    ERROR((short)-1);

    private Short id;

    BalanceStatus(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum BalanceStatus not settable");
    }
}
