/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.model.billing;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;

import java.util.List;

/**
 * The BillingProfile class.
 */
public class BillingProfile {

    private UserId userId;

    private PaymentInstrumentId defaultInstrumentId;

    private List<PaymentOption> paymentOptions;

    private List<Instrument> instruments;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public PaymentInstrumentId getDefaultInstrumentId() {
        return defaultInstrumentId;
    }

    public void setDefaultInstrumentId(PaymentInstrumentId defaultInstrumentId) {
        this.defaultInstrumentId = defaultInstrumentId;
    }

    public List<PaymentOption> getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(List<PaymentOption> paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
    }
}
