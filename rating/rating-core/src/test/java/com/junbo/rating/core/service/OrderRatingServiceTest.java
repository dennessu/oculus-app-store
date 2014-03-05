/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.rating.core.BaseTest;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.RatableItem;
import com.junbo.rating.spec.model.request.OrderRatingRequest;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
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
        RatingContext context = new RatingContext();
        context.setUserId(generateId());
        context.setCountry("US");
        context.setCurrency("USD");
        RatableItem item = new RatableItem();
        item.setOfferId(100L);
        item.setQuantity(1);
        context.setItems(new HashSet<RatableItem>());
        context.getItems().add(item);

        item = new RatableItem();
        item.setOfferId(generateId());
        item.setQuantity(2);
        context.getItems().add(item);

        OrderRatingRequest result = orderRatingService.orderRating(context);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLineItems().size(), 2);
        Assert.assertEquals(result.getOrderBenefit().getFinalAmount(), new BigDecimal("20.97"));
    }
}
