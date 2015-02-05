/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

/**
 * Created by dell on 2/5/2015.
 */
public class InstrumentDeleteResponse {
    private BillingProfile billingProfile;

    public BillingProfile getBillingProfile() {
        return billingProfile;
    }

    public void setBillingProfile(BillingProfile billingProfile) {
        this.billingProfile = billingProfile;
    }
}
