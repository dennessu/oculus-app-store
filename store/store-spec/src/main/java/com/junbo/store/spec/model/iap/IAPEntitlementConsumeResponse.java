/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.junbo.store.spec.model.BaseResponse;

/**
 * The IAPEntitlementConsumeResponse class.
 */
public class IAPEntitlementConsumeResponse extends BaseResponse {

    @JsonUnwrapped
    private Consumption consumption;

    public Consumption getConsumption() {
        return consumption;
    }

    public void setConsumption(Consumption consumption) {
        this.consumption = consumption;
    }
}
