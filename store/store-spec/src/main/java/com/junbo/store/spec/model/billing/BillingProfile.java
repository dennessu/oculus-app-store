/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.model.billing;

import java.util.List;

/**
 * The BillingProfile class.
 */
public class BillingProfile {

    private String defaultInstrumentId;

    private List<PaymentOption> paymentOptions;

    private List<Instrument> instruments;

    public String getDefaultInstrumentId() {
        return defaultInstrumentId;
    }

    public void setDefaultInstrumentId(String defaultInstrumentId) {
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
