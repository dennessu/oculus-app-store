/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.spec.model;

import com.junbo.common.id.PaymentInstrumentId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by chriszhu on 6/16/14.
 */
public class BillingPaymentInfo {
    @ApiModelProperty(required = true, position = 10, value = "The payment instrument.")
    private PaymentInstrumentId paymentInstrument;

    @ApiModelProperty(required = false, position = 20,
            value = "[Client Immutable]The distributed amount to this payment instrument.")
    private BigDecimal paymentAmount;

    public PaymentInstrumentId getPaymentInstrument() {
        return paymentInstrument;
    }

    public void setPaymentInstrument(PaymentInstrumentId paymentInstrument) {
        this.paymentInstrument = paymentInstrument;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}
