/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.mapper;

/**
 * payment api model.
 */
public enum PaymentAPI {
    AddPI,
    RemovePI,
    UpdatePI,
    Auth,
    Capture,
    Charge,
    Reverse,
    Refund
}
