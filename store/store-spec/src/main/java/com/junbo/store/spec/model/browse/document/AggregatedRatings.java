package com.junbo.store.spec.model.browse.document;

import java.util.Map;

/**
 * Created by facebook on 8/20/14.
 */
public class AggregatedRatings {

    private Double averageRating;

    private Long ratingsCount;

    private Long commentsCount;

    private Map<Integer, Long> ratingsHistogram;

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
