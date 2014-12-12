/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.ResourceMetaForDualWrite;

/**
 * payment event model.
 */
public class PaymentEvent extends ResourceMetaForDualWrite<Long> {
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private Long paymentId;
    private String type;
    private String status;
    private ChargeInfo chargeInfo;
    private String request;
    private String response;
    @JsonIgnore
    private String externalToken;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

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

    public String getExternalToken() {
        return externalToken;
    }

    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }

}
