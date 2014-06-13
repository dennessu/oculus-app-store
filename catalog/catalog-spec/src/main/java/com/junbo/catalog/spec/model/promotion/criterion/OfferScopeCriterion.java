/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion.criterion;

import com.junbo.common.jackson.annotation.OfferId;

import java.util.List;

/**
 * Offer Scope criterion.
 */
public class OfferScopeCriterion extends Criterion {
    @OfferId
    private List<String> offers;

    public List<String> getOffers() {
        return offers;
    }

    public void setOffers(List<String> offers) {
        this.offers = offers;
    }
}
