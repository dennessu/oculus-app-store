/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import java.util.Map;

/**
 * The AggregatedRatings class.
 */
public class AggregatedRatings {
    private String type;

    private Double averageRating;

    private Long ratingsCount;

    private Long commentsCount;

    private Map<Integer, Long> ratingsHistogram;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Long ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Map<Integer, Long> getRatingsHistogram() {
        return ratingsHistogram;
    }

    public void setRatingsHistogram(Map<Integer, Long> ratingsHistogram) {
        this.ratingsHistogram = ratingsHistogram;
    }
}
