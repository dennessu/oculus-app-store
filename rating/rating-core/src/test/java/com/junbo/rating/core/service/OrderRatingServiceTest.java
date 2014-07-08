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

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Created by lizwu on 2/27/14.
 */
public class OrderRatingServiceTest extends BaseTest {
    @Autowired
    private OrderRatingService orderRatingService;

    @Test
    public void testGeneral() {
        PriceRatingContext context = new PriceRatingContext();
        context.setRatingType(RatingType.ORDER);
        context.setUserId(generateId());
        context.setCountry("US");
        context.setCurrency(Currency.findByCode("USD"));
        RatableItem item = new RatableItem();
        item.setOfferId("100L");
        item.setQuantity(1);
        item.setShippingMethodId("400L");
        context.setItems(new HashSet<RatableItem>());
        context.getItems().add(item);

        item = new RatableItem();
        item.setOfferId("102L");
        item.setQuantity(1);
        item.setShippingMethodId("400L");
        context.getItems().add(item);

        item = new RatableItem();
        item.setOfferId("109L");
        item.setQuantity(1);
        item.setShippingMethodId("400L");
        context.getItems().add(item);

        orderRatingService.rate(context);
        RatingRequest result = context.buildResult();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLineItems().size(), 3);

        Assert.assertEquals(result.getRatingSummary().getDiscountAmount(), new BigDecimal("5.00"));
        Assert.assertEquals(result.getRatingSummary().getFinalAmount(), new BigDecimal("16.97"));

        Assert.assertEquals(result.getShippingSummary().getTotalShippingFee(), new BigDecimal("13.60"));
    }

    @Test
    public void testEntitlement() {
        PriceRatingContext context = new PriceRatingContext();
        context.setRatingType(RatingType.ORDER);
        context.setUserId(generateId());
        context.setCountry("US");
        context.setCurrency(Currency.findByCode("USD"));
        RatableItem item = new RatableItem();
        item.setOfferId("107L");
        item.setQuantity(1);
        context.setItems(new HashSet<RatableItem>());
        context.getItems().add(item);

        orderRatingService.rate(context);
        RatingRequest result = context.buildResult();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLineItems().size(), 1);

        Assert.assertEquals(result.getRatingSummary().getFinalAmount(), new BigDecimal("0.99"));

        Assert.assertEquals(result.getShippingSummary().getTotalShippingFee(), BigDecimal.ZERO);
    }
}
