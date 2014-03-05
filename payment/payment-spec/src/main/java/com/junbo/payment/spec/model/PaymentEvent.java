/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * payment event model.
 */
public class PaymentEvent {
    private Long paymentEventId;
    private Long paymentId;
    private String type;
    private String status;
    private ChargeInfo chargeInfo;
    private String request;
    private String response;
    @JsonIgnore
    public Long getPaymentEventId() {
        return paymentEventId;
    }
    @JsonIgnore
    public void setPaymentEventId(Long paymentEventId) {
        this.paymentEventId = paymentEventId;
    }
    @JsonIgnore
    public Long getPaymentId() {
        return paymentId;
    }
    @JsonIgnore
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ChargeInfo getChargeInfo() {
        return chargeInfo;
    }

    public void setChargeInfo(ChargeInfo chargeInfo) {
        this.chargeInfo = chargeInfo;
    }
    @JsonIgnore
    public String getRequest() {
        return request;
    }
    @JsonIgnore
    public void setRequest(String request) {
        this.request = request;
    }
    @JsonIgnore
    public String getResponse() {
        return response;
    }
    @JsonIgnore
    public void setResponse(String response) {
        this.response = response;
    }

}
