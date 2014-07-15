/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

/**
 * The BillingProfileUpdateRequest class.
 */
public class BillingProfileUpdateRequest {

    /**
     * The Operation enum.
     */
    public enum Operation {
        ADD_PI,
        UPDATE_PI,
        REMOVE_PI,
        SET_DEFAULT_PI
    }

    private Operation operation;

    private PaymentInstrument paymentInstrument;
}
