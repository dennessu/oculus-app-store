/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by xmchen on 2/20/14.
 */
@IdResourcePath(value = "/payment-instruments/{0}",
                resourceType = "payment-instruments",
                regex = "/payment-instruments/(?<id>[0-9A-Za-z]+)")
public class PaymentInstrumentId extends Id {

    public PaymentInstrumentId() {} {
    }

    public PaymentInstrumentId(Long value) {
        super(value);
    }
}
