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
    SETTLEMENT_SUBMIT_CREATED,
    SETTLEMENT_SUBMITTED,
    SETTLEMENT_SUBMIT_DECLINED,
    REVERSE_CREATED,
    REVERSED,
    REVERSE_DECLINED,
    SETTLE_CREATED,
    SETTLED,
    SETTLE_DECLINED,
    SETTLING,
    REFUND_CREATED,
    REFUNDED,
    REFUND_DECLINED,
    UNCONFIRMED,
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
