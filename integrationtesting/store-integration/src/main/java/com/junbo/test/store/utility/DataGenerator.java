/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.utility;

import com.junbo.common.id.ItemId;
import com.junbo.store.spec.model.browse.AddReviewRequest;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyLink;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import com.junbo.store.spec.model.external.casey.cms.CmsContentSlot;
import com.junbo.store.spec.model.external.casey.cms.CmsPage;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

    public String generateAndroidId() {
        String val = String.format("%016X", rand.nextLong());
        if (rand.nextBoolean()) {
            val = val.toLowerCase();
        } else {
            val = val.toUpperCase();
        }
        return val;
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

    public AddReviewRequest generateAddReviewRequest(ItemId itemId) {
        AddReviewRequest request = new AddReviewRequest();
        request.setItemId(itemId);
        request.setTitle("Test review title.." + RandomStringUtils.randomAlphabetic(5));
        request.setContent("Test review content.........." + RandomStringUtils.randomAlphabetic(30));
        request.setStarRatings(new HashMap<String, Integer>());
        request.getStarRatings().put("comfort", rand.nextInt(100));
        request.getStarRatings().put("quanlity", rand.nextInt(100));
        return request;
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

    public CmsPage genCmsPage(String path) {
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPath(path);
        cmsPage.setSlots(new HashMap<String, CmsContentSlot>());
        return cmsPage;
    }

    public SecureRandom random() {
        return rand;
    }
}
