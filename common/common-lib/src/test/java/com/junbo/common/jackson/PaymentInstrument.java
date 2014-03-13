/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.common.jackson.serializer.CascadeResource;

/**
 * PaymentInstrument.
 */
public class PaymentInstrument {
    @UserId
    private Long userId;

    private Long paymentInstrumentId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    @PaymentInstrumentId
    public void setPaymentInstrumentId(Long paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    @JsonProperty("paymentInstrumentId")
    @PaymentInstrumentId
    public CascadeResource getCascadePaymentInstrumentId() {
        return new CascadeResource(paymentInstrumentId, new Object[]{userId, paymentInstrumentId});
    }
}
