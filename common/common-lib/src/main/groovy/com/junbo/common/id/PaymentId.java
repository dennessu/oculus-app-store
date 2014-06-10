/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * payment id.
 */
@IdResourcePath(value = "/payment-transactions/{0}",
                resourceType = "payment-transactions",
                regex = "/payment-transactions/(?<id>[0-9A-Za-z]+)")
public class PaymentId extends Id {

    public PaymentId() {} {
    }

    public PaymentId(Long value) {
        super(value);
    }
}
