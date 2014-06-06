/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.rating.core.BaseTest;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.subscription.SubsRatingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by lizwu on 5/26/14.
 */
public class SubsRatingServiceTest extends BaseTest {
    @Autowired
    private SubsRatingService subsRatingService;

    @Test
    public void testDefaultProcessor() {
        SubsRatingContext context = new SubsRatingContext();
        context.setType(SubsRatingType.PURCHASE);
        context.setOfferId(100L);
        context.setCountry("US");
        context.setCurrency(Currency.USD);


        subsRatingService.rate(context);
        Assert.assertEquals(context.getAmount(), new BigDecimal("9.99"));
    }
}
