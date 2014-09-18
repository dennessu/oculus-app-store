/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

/**
 * Facebook Payment Action Type.
 */
public enum FacebookPaymentActionType {
    auth,
    capture,
    charge,
    settle,
    cancel,
    refund
}
