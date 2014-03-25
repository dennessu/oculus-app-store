/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import com.junbo.common.jackson.serializer.CompoundAware;

/**
 * compound PI id.
 */
public class PIId implements CompoundAware {
    private Long userId;
    private Long paymentInstrumentId;

    public PIId(){

    }

    public PIId(Long userId, Long paymentInstrumentId){
        this.userId = userId;
        this.paymentInstrumentId = paymentInstrumentId;
    }

    public Long getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    public void setPaymentInstrumentId(Long paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getPrimaryId() {
        return paymentInstrumentId;
    }
}
