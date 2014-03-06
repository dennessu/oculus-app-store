/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.enums;

/**
 * payment status enum.
 */
public enum PaymentStatus {
    AUTH_CREATED,
    AUTHORIZED,
    AUTH_DECLINED,
    AUTHORIZATION_EXPIRED,
    AUTHORIZING,
    SETTLE_CREATED,
    SETTLEMENT_SUBMITTED,
    SETTLEMENT_DECLINED,
    REVERSE_CREATED,
    REVERSED,
    REVERSE_DECLINED,
    SETTLED,
    SETTLING,
    UNRECOGNIZED;


    /**
     * brain tree payment status enum.
     */
    public enum BrainTreeStatus {
        AUTHORIZATION_EXPIRED,
        AUTHORIZED, AUTHORIZING, FAILED, GATEWAY_REJECTED, PROCESSOR_DECLINED,
        SETTLED, SETTLING, SUBMITTED_FOR_SETTLEMENT, UNRECOGNIZED, VOIDED;
    }
}
