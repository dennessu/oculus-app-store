/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.enums;

/**
 * payment event type enum.
 */
public enum PaymentEventType {
    AUTH_CREATE,
    AUTHORIZE,
    SUBMIT_SETTLE_CREATE,
    SUBMIT_SETTLE,
    REVERSE_CREATE,
    AUTH_REVERSE,
    SUBMIT_SETTLE_REVERSE,
    IMMEDIATE_SETTLE_CREATE,
    IMMEDIATE_SETTLE,
    REFUND_CREATE,
    REFUND,
    REPORT_EVENT,
    CREDIT_CREATE,
    CREDIT,
    NOTIFY
}
