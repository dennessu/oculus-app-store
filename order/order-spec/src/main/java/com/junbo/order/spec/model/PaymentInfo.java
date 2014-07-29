/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by chriszhu on 4/17/14.
 */
public class PaymentInfo {

    @ApiModelProperty(required = true, position = 10, value = "The payment instrument.")
    private PaymentInstrumentId paymentInstrument;

    @ApiModelProperty(required = false, position = 20,
            value = "[Client Immutable]The distributed amount to this payment instrument.")
    private BigDecimal paymentAmount;

    // urls for web payment
    @XSSFreeString
    @ApiModelProperty(required = true, position = 30, value = "[Client Immutable] The redirect url on success. ")
    private String successRedirectUrl;
    @XSSFreeString
    @ApiModelProperty(required = true, position = 40, value = "[Client Immutable] The redirect url on cancellation. ")
    private String cancelRedirectUrl;
    @XSSFreeString
    @ApiModelProperty(required = true, position = 50, value = "[Client Immutable] The redirect url on confirmation. ")
    private String providerConfirmUrl;
    // end of urls

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

    public String getSuccessRedirectUrl() {
        return successRedirectUrl;
    }

    public void setSuccessRedirectUrl(String successRedirectUrl) {
        this.successRedirectUrl = successRedirectUrl;
    }

    public String getCancelRedirectUrl() {
        return cancelRedirectUrl;
    }

    public void setCancelRedirectUrl(String cancelRedirectUrl) {
        this.cancelRedirectUrl = cancelRedirectUrl;
    }

    public String getProviderConfirmUrl() {
        return providerConfirmUrl;
    }

    public void setProviderConfirmUrl(String providerConfirmUrl) {
        this.providerConfirmUrl = providerConfirmUrl;
    }
}
