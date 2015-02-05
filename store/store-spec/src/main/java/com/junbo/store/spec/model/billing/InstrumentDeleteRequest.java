/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.junbo.common.id.PaymentInstrumentId;

import javax.ws.rs.QueryParam;

/**
 * Created by dell on 2/5/2015.
 */
public class InstrumentDeleteRequest {
    @QueryParam("paymentInstrumentId")
    private PaymentInstrumentId self;

    public PaymentInstrumentId getSelf() {
        return self;
    }

    public void setSelf(PaymentInstrumentId self) {
        this.self = self;
    }
}
