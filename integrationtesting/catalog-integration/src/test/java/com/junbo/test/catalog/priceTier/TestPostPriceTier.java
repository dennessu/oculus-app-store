/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.priceTier;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.test.catalog.impl.PriceTierServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.catalog.PriceTierService;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason
 * Time: 8/8/2014
 * For testing catalog post price tier API
 */
public class TestPostPriceTier extends BaseTestClass {

    private final String defaultLocale = "en_US";
    private LogHelper logger = new LogHelper(TestPostPriceTier.class);
    private PriceTierService priceTierService = PriceTierServiceImpl.instance();

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/price-tiers",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post a price tier",
            steps = {
                    "1. Post a price tier",
                    "2. Verify the return values as expected"
            }
    )
    @Test
    public void testPostPriceTier() throws Exception {
        prepareCatalogAdminToken();

        PriceTier priceTier = new PriceTier();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        SimpleLocaleProperties priceTierProperties = new SimpleLocaleProperties();
        priceTierProperties.setName("testPriceTier_" + RandomFactory.getRandomStringOfAlphabet(10));
        priceTierProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put(defaultLocale, priceTierProperties);

        Map<String, Map<String, BigDecimal>> prices = new HashMap<>();
        Map<String, BigDecimal> price1 = new HashMap<>();
        Map<String, BigDecimal> price2 = new HashMap<>();
        Map<String, BigDecimal> price3 = new HashMap<>();
        Map<String, BigDecimal> price4 = new HashMap<>();

        price1.put("USD", new BigDecimal(1000.00));
        price2.put("AUD", new BigDecimal(1000.00));
        price3.put("EUR", new BigDecimal(1000.00));
        price4.put("GBP", new BigDecimal(1000.00));
        price4.put("EUR", new BigDecimal(1000.00));

        prices.put("US", price1);
        prices.put("CA", price1);
        prices.put("MX", price1);
        prices.put("AU", price2);
        prices.put("IE", price3);
        prices.put("GB", price4);

        priceTier.setLocales(locales);
        priceTier.setPrices(prices);
        PriceTier priceTierPosted = priceTierService.postPriceTier(priceTier);

        verifyPriceTierEquality(priceTierPosted, priceTier);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/price-tiers",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post price tier with invalid values",
            steps = {
                    "1. Post price tier with invalid values(like null, not null and some invalid enum values)",
                    "2. verify the expected error"
            }
    )
    @Test
    public void testPostPriceTierInvalidScenarios() throws Exception {
        prepareCatalogAdminToken();

        PriceTier priceTier = new PriceTier();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();
        Map<String, Map<String, BigDecimal>> prices = new HashMap<>();
        Map<String, BigDecimal> price1 = new HashMap<>();
        Map<String, BigDecimal> price2 = new HashMap<>();
        Map<String, BigDecimal> price3 = new HashMap<>();
        Map<String, BigDecimal> price4 = new HashMap<>();

        //test everything is null
        verifyExpectedError(priceTier);

        //test prices is null
        SimpleLocaleProperties priceTierProperties = new SimpleLocaleProperties();
        priceTierProperties.setName("testPriceTier_" + RandomFactory.getRandomStringOfAlphabet(10));
        priceTierProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put(defaultLocale, priceTierProperties);

        priceTier.setLocales(locales);
        verifyExpectedError(priceTier);

        //test locales is null
        price1.put("USD", new BigDecimal(1000.00));
        price2.put("AUD", new BigDecimal(1000.00));
        price3.put("EUR", new BigDecimal(1000.00));
        price4.put("GBP", new BigDecimal(1000.00));
        price4.put("EUR", new BigDecimal(1000.00));

        prices.put("US", price1);
        prices.put("CA", price1);
        prices.put("MX", price1);
        prices.put("AU", price2);
        prices.put("IE", price3);
        prices.put("GB", price4);

        priceTier.setLocales(null);
        priceTier.setPrices(prices);

        verifyExpectedError(priceTier);

        //test some locale is null
        locales.put("zh_CN", null);
        priceTier.setLocales(locales);
        verifyExpectedError(priceTier);

        //test the locale name is null
        priceTierProperties = new SimpleLocaleProperties();
        priceTierProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));

        locales.clear();
        locales.put(defaultLocale, priceTierProperties);
        priceTier.setLocales(locales);

        verifyExpectedError(priceTier);

        //test the rev is not null
        priceTierProperties.setName("testPriceTier_" + RandomFactory.getRandomStringOfAlphabet(10));
        locales.put(defaultLocale, priceTierProperties);
        priceTier.setLocales(locales);
        priceTier.setRev("1");

        verifyExpectedError(priceTier);

        //test self id is not null
        priceTier.setRev(null);
        priceTier.setId("randomId");

        verifyExpectedError(priceTier);

        //test some price in some country is less than 0
        price1.put("USD", new BigDecimal(-1000.00));
        price2.put("AUD", new BigDecimal(-1000.00));
        price3.put("EUR", new BigDecimal(1000.00));
        price4.put("GBP", new BigDecimal(-1000.00));
        price4.put("EUR", new BigDecimal(1000.00));

        prices.put("US", price1);
        prices.put("CA", price1);
        prices.put("MX", price1);
        prices.put("AU", price2);
        prices.put("IE", price3);
        prices.put("GB", price4);

        priceTier.setId(null);
        priceTier.setPrices(prices);
        verifyExpectedError(priceTier);

        //test some price is null
        price1.put("USD", new BigDecimal(1000.00));
        price2.put("AUD", new BigDecimal(1000.00));
        price3.put("EUR", new BigDecimal(1000.00));
        price4.put("GBP", new BigDecimal(1000.00));
        price4.put("EUR", new BigDecimal(1000.00));

        prices.put("US", price1);
        prices.put("CA", price1);
        prices.put("MX", price1);
        prices.put("AU", price2);
        prices.put("IE", price3);
        prices.put("GB", price4);
        prices.put("CN", null);

        priceTier.setPrices(prices);
        verifyExpectedError(priceTier);
    }

    private void verifyExpectedError(PriceTier priceTier) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            priceTierService.postPriceTier(priceTier, 400);
            Assert.fail("Post price tier should fail");
        }
        catch (Exception ex) {
            logger.logInfo("expected exception" + ex);
        }
    }

}
