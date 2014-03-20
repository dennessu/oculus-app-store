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
 * TestId.
 */
public class PaymentInstrument {
    @UserId
    private Long userId;

    private Long paymentInstrumentId;

    private Long paymentInstrumentId2;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    @JsonProperty("self")
    @PaymentInstrumentId
    public void setPaymentInstrumentId(Long paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    @JsonProperty("self")
    @PaymentInstrumentId
    public CascadeResource getCascadePaymentInstrumentId() {
        return new CascadeResource(paymentInstrumentId, new Object[]{userId, paymentInstrumentId});
    }

    public Long getPaymentInstrumentId2() {
        return paymentInstrumentId2;
    }

    @PaymentInstrumentId
    public void setPaymentInstrumentId2(Long paymentInstrumentId2) {
        this.paymentInstrumentId2 = paymentInstrumentId2;
    }

    @JsonProperty("paymentInstrumentId2")
    @PaymentInstrumentId
    public CascadeResource getCascadePaymentInstrumentId2() {
        return paymentInstrumentId2 == null ? null
                : new CascadeResource(paymentInstrumentId, new Object[]{userId, paymentInstrumentId2});
    }
}
