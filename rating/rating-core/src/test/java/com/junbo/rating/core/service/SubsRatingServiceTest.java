/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.rating.core.BaseTest;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.subscription.DurationUnit;
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
        context.setSubsRatingType(SubsRatingType.PURCHASE);
        context.setOfferId("100L");
        context.setCountry("US");
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        currency.setNumberAfterDecimal(2);
        context.setCurrency(currency);


        subsRatingService.rate(context);
        Assert.assertEquals(context.getAmount(), new BigDecimal("9.99"));
    }

    @Test
    public void testCycleProcessor() {
        SubsRatingContext context = new SubsRatingContext();
        context.setSubsRatingType(SubsRatingType.CYCLE);
        context.setOfferId("100L");
        context.setCountry("US");
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        currency.setNumberAfterDecimal(2);
        context.setCurrency(currency);
        context.setCycleCount(1);

        subsRatingService.rate(context);
        Assert.assertEquals(context.getAmount(), new BigDecimal("0.99"));
    }

    @Test
    public void testExtendProcessor() {
        SubsRatingContext context = new SubsRatingContext();
        context.setSubsRatingType(SubsRatingType.EXTEND);
        context.setOfferId("100L");
        context.setCountry("US");
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        currency.setNumberAfterDecimal(2);
        context.setCurrency(currency);
        context.setExtensionNum(10);
        context.setExtensionUnit(DurationUnit.DAY);

        subsRatingService.rate(context);
        Assert.assertEquals(context.getAmount(), BigDecimal.ZERO);
    }
}
