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
import com.junbo.store.spec.model.external.casey.cms.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.util.*;

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

    public CaseyReview generateCaseyReview(String userId, ItemId itemId) {
        CaseyReview caseyReview = new CaseyReview();
        caseyReview.setReview("review text:" + RandomStringUtils.randomAlphabetic(15));
        caseyReview.setReviewTitle("title:" + RandomStringUtils.randomAlphabetic(15));
        caseyReview.setResourceType("item");
        caseyReview.setUser(new CaseyLink(userId));
        caseyReview.setPostedDate(new Date(System.currentTimeMillis() / 1000L * 1000));
        caseyReview.setSelf(new CaseyLink(UUID.randomUUID().toString()));
        if (itemId != null) {
            CaseyLink caseyLink = new CaseyLink();
            caseyLink.setId(itemId.getValue());
            caseyReview.setResource(caseyLink);
        }

        CaseyReview.Rating qualityRating = new CaseyReview.Rating();
        qualityRating.setType("quality");
        qualityRating.setScore(rand.nextInt(100));

        CaseyReview.Rating comfortRating = new CaseyReview.Rating();
        comfortRating.setType("comfort");
        comfortRating.setScore(rand.nextInt(100));

        caseyReview.setRatings(Arrays.asList(qualityRating, comfortRating));
        return caseyReview;
    }

    public AddReviewRequest generateAddReviewRequest(ItemId itemId) {
        AddReviewRequest request = new AddReviewRequest();
        request.setItemId(itemId);
        request.setTitle("Test review title.." + RandomStringUtils.randomAlphabetic(5));
        request.setContent("Test review content.........." + RandomStringUtils.randomAlphabetic(30));
        request.setStarRatings(new HashMap<String, Integer>());
        request.getStarRatings().put("comfort", rand.nextInt(5) + 1);
        request.getStarRatings().put("quality", rand.nextInt(5) + 1);
        return request;
    }

    public CaseyAggregateRating generateCaseyAggregateRating(String type) {
        CaseyAggregateRating aggregateRating = new CaseyAggregateRating();
        aggregateRating.setAverage((double) rand.nextInt(100));
        aggregateRating.setCount((long) rand.nextInt(10000));
        aggregateRating.setType(type);
        aggregateRating.setHistogram(new Long[10]);
        for (int i = 0; i < aggregateRating.getHistogram().length; ++i) {
            aggregateRating.getHistogram()[i] = (long) rand.nextInt(1000);
        }
        return aggregateRating;
    }

    public CmsPage genCmsPage(String path, String label, List<String> slots) {
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSelf(new CaseyLink());
        cmsPage.getSelf().setId(UUID.randomUUID().toString());
        cmsPage.setPath(path);
        cmsPage.setLabel(label);
        cmsPage.setSlots(new HashMap<String, CmsContentSlot>());
        if (slots != null) {
            for (String slot : slots) {
                cmsPage.getSlots().put(slot, new CmsContentSlot());
            }
        }
        return cmsPage;
    }

    public CmsSchedule genCmsSchedule(String pageId) {
        CmsSchedule cmsSchedule = new CmsSchedule();
        CaseyLink caseyLink = new CaseyLink();
        caseyLink.setId(pageId);
        cmsSchedule.setSelf(caseyLink);
        cmsSchedule.setSlots(new TreeMap<String, CmsScheduleContent>());
        return cmsSchedule;
    }

    public CmsScheduleContent genCmsScheduleContent(String name, String value) {
        CaseyContentItemString string = new CaseyContentItemString();
        string.setLocales(Collections.singletonMap("en_US", value));
        ContentItem contentItem = new ContentItem();
        contentItem.setType(ContentItem.Type.string.name());
        contentItem.setStrings(Arrays.asList(string));
        CmsContent cmsContent = new CmsContent();
        cmsContent.setContents(Collections.singletonMap(name, contentItem));
        CmsScheduleContent cmsScheduleContent = new CmsScheduleContent();
        cmsScheduleContent.setContent(cmsContent);
        return cmsScheduleContent;
    }

    //public CmsSchedule genCmsSchedule(String pageId)

    public CmsCampaign genCmsCampaign(String label, List<Placement> placements) {
        CmsCampaign cmsCampaign = new CmsCampaign();
        cmsCampaign.setPlacements(placements);
        cmsCampaign.setLabel(label);
        return cmsCampaign;
    }

    public SecureRandom random() {
        return rand;
    }
}
