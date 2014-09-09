/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.emulator.casey.spec.model;

import com.junbo.store.spec.model.external.casey.CaseyReview;

import java.util.List;

/**
 * The UpdateReviewRequest class.
 */
public class UpdateReviewRequest {

    private List<CaseyReview> caseyReviews;

    public List<CaseyReview> getCaseyReviews() {
        return caseyReviews;
    }

    public void setCaseyReviews(List<CaseyReview> caseyReviews) {
        this.caseyReviews = caseyReviews;
    }
}
