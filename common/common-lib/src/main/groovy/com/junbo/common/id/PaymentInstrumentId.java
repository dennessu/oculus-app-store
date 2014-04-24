/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by xmchen on 2/20/14.
 */
@IdResourcePath("/payment-instruments/{0}")
public class PaymentInstrumentId extends Id {

    public PaymentInstrumentId() {} {
    }

    public PaymentInstrumentId(Long value) {
        super(value);
    }
}