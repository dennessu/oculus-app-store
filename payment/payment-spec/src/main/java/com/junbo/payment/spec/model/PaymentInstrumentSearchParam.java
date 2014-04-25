/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import javax.ws.rs.QueryParam;

/**
 * payment instrument search model.
 */
public class PaymentInstrumentSearchParam {
    @QueryParam("userId")
    private Long userId;
    @QueryParam("type")
    private String type;

    public PaymentInstrumentSearchParam() {
    }

    public PaymentInstrumentSearchParam(Long userId, String type, String status) {
        this.userId = userId;
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
