/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.rating.core.BaseTest;
import com.junbo.rating.core.context.PriceRatingContext;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.RatableItem;
import com.junbo.rating.spec.model.RatingType;
import com.junbo.rating.spec.model.priceRating.RatingRequest;
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
        PriceRatingContext context = new PriceRatingContext();
        context.setRatingType(RatingType.OFFER);
        context.setUserId(generateId());
        context.setCountry("US");
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        currency.setNumberAfterDecimal(2);
        context.setCurrency(currency);
        RatableItem item = new RatableItem();
        item.setOfferId("100L");
        context.setItems(new HashSet<RatableItem>());
        context.getItems().add(item);

        offerRatingService.rate(context);
        RatingRequest result = context.buildResult();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLineItems().size(), 1);
    }
}
