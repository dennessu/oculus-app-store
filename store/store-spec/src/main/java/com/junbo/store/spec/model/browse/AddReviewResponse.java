/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.userlog.EntityLoggable;
import com.junbo.store.spec.model.browse.document.Review;

/**
 * The AddReviewResponse class.
 */
public class AddReviewResponse implements EntityLoggable {

    private Review review;

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    @JsonIgnore
    @Override
    public String getEntityLogId() {
        return review == null || review.getSelf() == null || review.getSelf().getId() == null ? null : review.getSelf().getId();
    }
}
