/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.review;

import java.util.Map;

/**
 * The AggregateRating class.
 */
public class AggregateRating {

    /**
     * The AggregateRatingType enum.
     */
    public enum AggregateRatingType {
        STAR_RATING,
        THUMBS_RATING
    }

    private Double bayesianMeanRating;
    private Long commentCount;
    private Long ratingsCount;
    private Map<Long, Long> starRatings;;
    private Double averageStarRating;
    private Long thumbsDownCount;
    private Long thumbsUpCount;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getBayesianMeanRating() {
        return bayesianMeanRating;
    }

    public void setBayesianMeanRating(Double bayesianMeanRating) {
        this.bayesianMeanRating = bayesianMeanRating;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Long ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public Map<Long, Long> getStarRatings() {
        return starRatings;
    }

    public void setStarRatings(Map<Long, Long> starRatings) {
        this.starRatings = starRatings;
    }

    public Double getAverageStarRating() {
        return averageStarRating;
    }

    public void setAverageStarRating(Double averageStarRating) {
        this.averageStarRating = averageStarRating;
    }

    public Long getThumbsDownCount() {
        return thumbsDownCount;
    }

    public void setThumbsDownCount(Long thumbsDownCount) {
        this.thumbsDownCount = thumbsDownCount;
    }

    public Long getThumbsUpCount() {
        return thumbsUpCount;
    }

    public void setThumbsUpCount(Long thumbsUpCount) {
        this.thumbsUpCount = thumbsUpCount;
    }
}
