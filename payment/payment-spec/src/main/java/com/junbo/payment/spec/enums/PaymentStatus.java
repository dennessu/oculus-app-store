/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * payment status enum.
 */
public enum PaymentStatus implements Identifiable<Short> {
    AUTH_CREATED((short)1),
    AUTHORIZED((short)2),
    AUTH_DECLINED((short)3),
    SETTLEMENT_SUBMIT_CREATED((short)6),
    SETTLEMENT_SUBMITTED((short)7),
    SETTLEMENT_SUBMIT_DECLINED((short)8),
    REVERSE_CREATED((short)9),
    REVERSED((short)10),
    REVERSE_DECLINED((short)11),
    SETTLE_CREATED((short)12),
    SETTLED((short)13),
    SETTLE_DECLINED((short)14),
    SETTLING((short)15),
    REFUND_CREATED((short)16),
    REFUNDED((short)17),
    REFUND_DECLINED((short)18),
    UNCONFIRMED((short)19),
    CREDIT_CREATED((short)20),
    CREDIT_DECLINED((short)21),
    UNRECOGNIZED((short)22),
    CHARGE_BACK((short)23),
    DISCREPANT((short)24),
    RISK_PENDING((short)25),
    RISK_ASYNC_REJECT((short)26);

    private final Short id;

    PaymentStatus(Short id) {
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


    /**
     * brain tree payment status enum.
     */
    public enum BrainTreeStatus {
        AUTHORIZATION_EXPIRED,
        AUTHORIZED, AUTHORIZING, FAILED, GATEWAY_REJECTED, PROCESSOR_DECLINED,
        SETTLED, SETTLING, SUBMITTED_FOR_SETTLEMENT, UNRECOGNIZED, VOIDED;
    }
}
