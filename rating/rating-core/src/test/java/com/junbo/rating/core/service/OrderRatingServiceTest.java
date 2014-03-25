/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.rating.core.BaseTest;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.Currency;
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
        context.setCurrency(Currency.findByCode("USD"));
        RatableItem item = new RatableItem();
        item.setOfferId(100L);
        item.setQuantity(1);
        item.setShippingMethodId(400L);
        context.setItems(new HashSet<RatableItem>());
        context.getItems().add(item);

        item = new RatableItem();
        item.setOfferId(102L);
        item.setQuantity(2);
        item.setShippingMethodId(400L);
        context.getItems().add(item);

        item = new RatableItem();
        item.setOfferId(109L);
        item.setQuantity(2);
        item.setShippingMethodId(400L);
        context.getItems().add(item);

        OrderRatingRequest result = orderRatingService.orderRating(context);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLineItems().size(), 3);

        Assert.assertEquals(result.getOrderBenefit().getDiscountAmount(), new BigDecimal("5.00"));
        Assert.assertEquals(result.getOrderBenefit().getFinalAmount(), new BigDecimal("28.95"));

        Assert.assertEquals(result.getShippingBenefit().getShippingFee(), new BigDecimal("20.80"));
    }
}
