/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.junbo.common.id.OfferId;

import javax.ws.rs.QueryParam;

/**
 * The BillingProfileGetRequest class.
 */
public class BillingProfileGetRequest {

    @QueryParam("offerId")
    private OfferId offer;

    public OfferId getOffer() {
        return offer;
    }

    public void setOffer(OfferId offer) {
        this.offer = offer;
    }
}
