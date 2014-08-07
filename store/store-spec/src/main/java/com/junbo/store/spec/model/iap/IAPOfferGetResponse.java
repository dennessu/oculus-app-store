/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.iap;

import com.junbo.common.model.Results;
import com.junbo.store.spec.model.Offer;

/**
 * The IAPOfferGetResponse class.
 */
public class IAPOfferGetResponse {

    Results<Offer> offers;

    public Results<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Results<Offer> offers) {
        this.offers = offers;
    }
}
