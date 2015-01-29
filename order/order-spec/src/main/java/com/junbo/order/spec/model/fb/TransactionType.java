/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.fb;

/**
 * S = Spend, R = Refunds, C = Chargebacks, N =
 * Declines, D = CB > 90 days, K = CB Reversals
 * w/in 90 day, J = CB Reversal out of window.
 */
public enum TransactionType {
    S,
    R,
    C,
    N,
    D,
    K,
    J
}
