/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.userlog.EntityLoggable;

/**
 * The InstrumentUpdateResponse class.
 */
public class InstrumentUpdateResponse implements EntityLoggable {

    private BillingProfile billingProfile;

    @JsonIgnore
    private PaymentInstrumentId updatedInstrument;

    public BillingProfile getBillingProfile() {
        return billingProfile;
    }

    public void setBillingProfile(BillingProfile billingProfile) {
        this.billingProfile = billingProfile;
    }

    public PaymentInstrumentId getUpdatedInstrument() {
        return updatedInstrument;
    }

    public void setUpdatedInstrument(PaymentInstrumentId updatedInstrument) {
        this.updatedInstrument = updatedInstrument;
    }

    @JsonIgnore
    @Override
    public String getEntityLogId() {
        return updatedInstrument == null ? null : updatedInstrument.toString();
    }
}
