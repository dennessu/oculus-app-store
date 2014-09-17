/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.emulator.casey.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.store.spec.model.external.casey.CaseyLink;
import com.junbo.store.spec.model.external.casey.CaseyReview;

import java.util.Date;

/**
 * The CaseyReviewExtend class.
 */
public class CaseyReviewExtend extends CaseyReview {

    @JsonProperty("user")
    @JsonIgnore(false)
    public CaseyLink getUser() {
        return super.getUser();
    }

    @JsonProperty("postedDate")
    @JsonIgnore(false)
    public Date getPostedDate() {
        return super.getPostedDate();
    }
    @JsonProperty("self")
    @JsonIgnore(false)
    public CaseyLink getSelf() {
        return super.getSelf();
    }

    public CaseyReviewExtend(CaseyReview caseyReview) {
        setCountry(caseyReview.getCountry());
        setLocale(caseyReview.getLocale());
        setPostedDate(caseyReview.getPostedDate());
        setRatings(caseyReview.getRatings());
        setReviewTitle(caseyReview.getReviewTitle());
        setReview(caseyReview.getReview());
        setUser(caseyReview.getUser());
        setResourceType(caseyReview.getResourceType());
        setResource(caseyReview.getResource());
        setSelf(caseyReview.getSelf());
    }
}
