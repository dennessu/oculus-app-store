/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.utility;

import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyLink;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * The DataGenerator class.
 */
public class DataGenerator {

    private static DataGenerator innerInstance = new DataGenerator();

    private SecureRandom rand = new SecureRandom();

    public static DataGenerator instance() {
        return innerInstance;
    }

    public CaseyReview generateCaseyReview(String userId) {
        CaseyReview caseyReview = new CaseyReview();
        caseyReview.setReview("review text:" + RandomStringUtils.randomAlphabetic(15));
        caseyReview.setReviewTitle("title:" + RandomStringUtils.randomAlphabetic(15));
        caseyReview.setResourceType("item");
        caseyReview.setUser(new CaseyLink(userId));
        caseyReview.setPostedDate(new Date(System.currentTimeMillis() / 1000L * 1000));
        caseyReview.setSelf(new CaseyLink(UUID.randomUUID().toString()));

        CaseyReview.Rating qualityRating = new CaseyReview.Rating();
        qualityRating.setType("quality");
        qualityRating.setScore(rand.nextInt(10));

        CaseyReview.Rating comfortRating = new CaseyReview.Rating();
        comfortRating.setType("comfort");
        comfortRating.setScore(rand.nextInt(10));

        caseyReview.setRatings(Arrays.asList(qualityRating, comfortRating));
        return caseyReview;
    }

    public CaseyAggregateRating generateCaseyAggregateRating(String type) {
        CaseyAggregateRating aggregateRating = new CaseyAggregateRating();
        aggregateRating.setAverage((double) rand.nextInt(10));
        aggregateRating.setCount((long) rand.nextInt(10000));
        aggregateRating.setType(type);
        aggregateRating.setHistogram(new Long[10]);
        for (int i = 0; i < aggregateRating.getHistogram().length; ++i) {
            aggregateRating.getHistogram()[i] = (long) rand.nextInt(1000);
        }
        return aggregateRating;
    }

}
