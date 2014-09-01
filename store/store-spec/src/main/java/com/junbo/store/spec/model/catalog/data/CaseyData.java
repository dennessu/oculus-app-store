/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.catalog.data;

import com.junbo.store.spec.model.browse.ReviewsResponse;
import com.junbo.store.spec.model.browse.document.AggregatedRatings;

import java.util.List;

/**
 * CaseyData.
 */
public class CaseyData {
    private List<AggregatedRatings> aggregatedRatings;

    private ReviewsResponse reviewsResponse;

    public List<AggregatedRatings> getAggregatedRatings() {
        return aggregatedRatings;
    }

    public void setAggregatedRatings(List<AggregatedRatings> aggregatedRatings) {
        this.aggregatedRatings = aggregatedRatings;
    }

    public ReviewsResponse getReviewsResponse() {
        return reviewsResponse;
    }

    public void setReviewsResponse(ReviewsResponse reviewsResponse) {
        this.reviewsResponse = reviewsResponse;
    }
}
