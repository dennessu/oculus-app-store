/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.PaymentInstrumentTypeId;

/**
 * payment instrument type model.
 */
public class PaymentInstrumentType {
    @JsonProperty("self")
    @PaymentInstrumentTypeId
    private String name;
    private String recurring;
    private String refundable;
    private String authorizable;
    private String defaultable;

    public String getDefaultable() {
        return defaultable;
    }

    public void setDefaultable(String defaultable) {
        this.defaultable = defaultable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecurring() {
        return recurring;
    }

    public void setRecurring(String recurring) {
        this.recurring = recurring;
    }

    public String getRefundable() {
        return refundable;
    }

    public void setRefundable(String refundable) {
        this.refundable = refundable;
    }

    public String getAuthorizable() {
        return authorizable;
    }

    public void setAuthorizable(String authorizable) {
        this.authorizable = authorizable;
    }
}
