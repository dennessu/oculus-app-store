/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.emulator.casey.spec.model;

import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;

import java.util.List;

/**
 * The UpdateAggregateRatingRequest class.
 */
public class UpdateAggregateRatingRequest {

    private List<CaseyAggregateRating> aggregatedRatings;

    public List<CaseyAggregateRating> getAggregatedRatings() {
        return aggregatedRatings;
    }

    public void setAggregatedRatings(List<CaseyAggregateRating> aggregatedRatings) {
        this.aggregatedRatings = aggregatedRatings;
    }
}
