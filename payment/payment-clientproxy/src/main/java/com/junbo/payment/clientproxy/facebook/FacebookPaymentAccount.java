/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.QueryParam;

/**
 * Facebook Payment Account.
 */
public class FacebookPaymentAccount {
    @JsonProperty
    private String id;
    @QueryParam("payer_id")
    @JsonProperty("payer_id")
    private String payerId;
    @QueryParam("payer_email")
    @JsonProperty("payer_email")
    private String payerEmail;
    @QueryParam("payer_address")
    @JsonProperty("payer_address")
    private FacebookAddress payerAddress;

    public FacebookAddress getPayerAddress() {
        return payerAddress;
    }

    public void setPayerAddress(FacebookAddress payerAddress) {
        this.payerAddress = payerAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }
}
