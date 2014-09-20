/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.catalog.data;

import com.junbo.store.spec.model.browse.ReviewsResponse;
import com.junbo.store.spec.model.browse.document.AggregatedRatings;

import java.util.Map;

/**
 * CaseyData.
 */
public class CaseyData {
    private Map<String, AggregatedRatings> aggregatedRatings;

    private ReviewsResponse reviewsResponse;

    public Map<String, AggregatedRatings> getAggregatedRatings() {
        return aggregatedRatings;
    }

    public void setAggregatedRatings(Map<String, AggregatedRatings> aggregatedRatings) {
        this.aggregatedRatings = aggregatedRatings;
    }

    public ReviewsResponse getReviewsResponse() {
        return reviewsResponse;
    }

    public void setReviewsResponse(ReviewsResponse reviewsResponse) {
        this.reviewsResponse = reviewsResponse;
    }
}
