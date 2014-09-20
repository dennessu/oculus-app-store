/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * CaseyReview.
 */
public class CaseyReview extends BaseCaseyModel {
    private List<Rating> ratings;
    private String reviewTitle;
    private String review;
    private String resourceType;
    private String country;
    private String locale;
    private CaseyLink resource;
    private CaseyLink user;
    private Date postedDate;
    private CaseyLink self;

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public CaseyLink getResource() {
        return resource;
    }

    public void setResource(CaseyLink resource) {
        this.resource = resource;
    }

    @JsonIgnore
    public CaseyLink getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(CaseyLink user) {
        this.user = user;
    }

    @JsonIgnore
    public Date getPostedDate() {
        return postedDate;
    }

    @JsonProperty("postedDate")
    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    @JsonIgnore
    public CaseyLink getSelf() {
        return self;
    }

    @JsonProperty("self")
    public void setSelf(CaseyLink self) {
        this.self = self;
    }

    /**
     * Rating.
     */
    public static class Rating {
        private Integer score;
        private String type;

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * Rating type.
     */
    public static enum  RatingType {
        quality,
        comfort
    }
}
