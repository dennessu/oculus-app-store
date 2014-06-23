/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.adyen;

/**
 * adyen event code need to handle.
 */
public enum AdyenEventCode {
    AUTHORISATION,
    CANCELLATION,
    REFUND,
    CANCEL_OR_REFUND,
    CAPTURE,
    REFUNDED_REVERSED,
    CAPTURE_FAILED,
    REFUND_FAILED,
    REQUEST_FOR_INFORMATION,
    NOTIFICATION_OF_CHARGEBACK,
    ADVICE_OF_DEBIT,
    CHARGEBACK,
    CHARGEBACK_REVERSED,
    REPORT_AVAILABLE
}
