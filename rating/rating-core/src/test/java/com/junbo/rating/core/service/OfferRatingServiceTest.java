/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.rating.core.BaseTest;
import com.junbo.rating.core.builder.RatingResultBuilder;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.RatableItem;
import com.junbo.rating.spec.model.request.OfferRatingRequest;
import com.junbo.rating.spec.model.request.RatingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;

/**
 * Created by lizwu on 2/20/14.
 */
public class OfferRatingServiceTest extends BaseTest {
    @Autowired
    private OfferRatingService offerRatingService;

    @Test
    public void testGeneral() {
        RatingContext context = new RatingContext();
        context.setUserId(generateId());
        context.setCurrency(Currency.findByCode("USD"));
        RatableItem item = new RatableItem();
        item.setOfferId(100L);
        context.setItems(new HashSet<RatableItem>());
        context.getItems().add(item);

        offerRatingService.offerRating(context);
        RatingRequest result = RatingResultBuilder.buildForOffers(context);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLineItems().size(), 1);
    }
}
