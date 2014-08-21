/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.Review;

import java.util.List;

/**
 * The ReviewsResponse class.
 */
public class ReviewsResponse {

    private List<Review> reviews;

    private String reviewsNextPageUrl;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getReviewsNextPageUrl() {
        return reviewsNextPageUrl;
    }

    public void setReviewsNextPageUrl(String reviewsNextPageUrl) {
        this.reviewsNextPageUrl = reviewsNextPageUrl;
    }
}
