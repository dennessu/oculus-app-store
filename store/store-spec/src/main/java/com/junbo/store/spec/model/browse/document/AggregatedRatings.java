package com.junbo.store.spec.model.browse.document;

/**
 * Created by facebook on 8/20/14.
 */
public class AggregatedRatings {

    private Double averageRating;

    private Long ratingsCount;

    private Long commentsCount;

    private Long oneStarRatings;

    private Long twoStarRatings;

    private Long threeStarRatings;

    private Long fourStarRatings;

    private Long fiveStarRatings;

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

    public Long getOneStarRatings() {
        return oneStarRatings;
    }

    public void setOneStarRatings(Long oneStarRatings) {
        this.oneStarRatings = oneStarRatings;
    }

    public Long getTwoStarRatings() {
        return twoStarRatings;
    }

    public void setTwoStarRatings(Long twoStarRatings) {
        this.twoStarRatings = twoStarRatings;
    }

    public Long getThreeStarRatings() {
        return threeStarRatings;
    }

    public void setThreeStarRatings(Long threeStarRatings) {
        this.threeStarRatings = threeStarRatings;
    }

    public Long getFourStarRatings() {
        return fourStarRatings;
    }

    public void setFourStarRatings(Long fourStarRatings) {
        this.fourStarRatings = fourStarRatings;
    }

    public Long getFiveStarRatings() {
        return fiveStarRatings;
    }

    public void setFiveStarRatings(Long fiveStarRatings) {
        this.fiveStarRatings = fiveStarRatings;
    }
}
