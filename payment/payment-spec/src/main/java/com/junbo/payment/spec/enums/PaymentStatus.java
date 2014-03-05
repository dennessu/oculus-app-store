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
    SETTLE_CREATED,
    SETTLEMENT_SUBMITTED,
    SETTLEMENT_DECLINED,
    REVERSE_CREATED,
    REVERSED,
    REVERSE_DECLINED
}
