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
import com.junbo.catalog.common.util.CloneUtils;
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
 * For testing catalog put price tier(s) API
 */
public class TestPutPriceTier extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPutPriceTier.class);
    private PriceTierService priceTierService = PriceTierServiceImpl.instance();
    private final String defaultLocale = "en_US";

    @Property(
            priority = Priority.Dailies,
            features = "Put v1/price-tiers/{priceTierId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put price tier successfully",
            steps = {
                    "1. Prepare a default price tier",
                    "2. Put the price tier with corrected fields values",
                    "3. Verify the action could be successful"
            }
    )
    @Test
    public void testPutPriceTier() throws Exception {
        prepareCatalogAdminToken();

        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        //Prepare an price tier
        PriceTier priceTier = priceTierService.postDefaultPriceTier();

        //put the price tier
        SimpleLocaleProperties priceTierProperties = new SimpleLocaleProperties();
        String newName = "testPriceTier_" + RandomFactory.getRandomStringOfAlphabet(10);
        String newDescription = RandomFactory.getRandomStringOfAlphabetOrNumeric(30);
        priceTierProperties.setName(newName);
        priceTierProperties.setDescription(newDescription);
        locales.put(defaultLocale, priceTierProperties);
        priceTier.setLocales(locales);

        PriceTier priceTierPut = priceTierService.updatePriceTier(priceTier.getId(), priceTier);

        //Verification
        verifyPriceTierEquality(priceTierPut, priceTier);

        //put prices
        priceTier = CloneUtils.clone(priceTierPut);
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

        priceTier.setPrices(prices);

        priceTierPut = priceTierService.updatePriceTier(priceTierPut.getId(), priceTier);

        //Verification
        verifyPriceTierEquality(priceTierPut, priceTier);

    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Put v1/price-tiers/{priceTierId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put price tier invalid scenarios",
            steps = {
                    "1. Prepare a default price tier",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutPriceTierInvalidScenarios() throws Exception {
        prepareCatalogAdminToken();

        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();
        Map<String, Map<String, BigDecimal>> prices = new HashMap<>();
        Map<String, BigDecimal> price1 = new HashMap<>();
        Map<String, BigDecimal> price2 = new HashMap<>();
        Map<String, BigDecimal> price3 = new HashMap<>();
        Map<String, BigDecimal> price4 = new HashMap<>();

        //Prepare an price tier
        PriceTier priceTier = priceTierService.postDefaultPriceTier();
        String priceTierId = priceTier.getId();
        String originalRev = priceTier.getRev();

        //update itself id
        priceTier.setId("1L");
        verifyExpectedError(priceTierId, priceTier);

        //test rev
        priceTier.setRev("0");
        verifyExpectedError(priceTierId, priceTier);

        //test everything is null
        priceTier.setRev(originalRev);
        priceTier.setPrices(null);
        priceTier.setLocales(null);
        verifyExpectedError(priceTier.getId(), priceTier);

        //test prices is null
        SimpleLocaleProperties priceTierProperties = new SimpleLocaleProperties();
        priceTierProperties.setName("testPriceTier_" + RandomFactory.getRandomStringOfAlphabet(10));
        priceTierProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put(defaultLocale, priceTierProperties);

        priceTier.setLocales(locales);
        verifyExpectedError(priceTier.getId(), priceTier);

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

        verifyExpectedError(priceTier.getId(), priceTier);

        //test some locale is null
        locales.put("zh_CN", null);
        priceTier.setLocales(locales);
        verifyExpectedError(priceTier.getId(), priceTier);

        //test the locale name is null
        priceTierProperties = new SimpleLocaleProperties();
        priceTierProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));

        locales.clear();
        locales.put(defaultLocale, priceTierProperties);
        priceTier.setLocales(locales);

        verifyExpectedError(priceTier.getId(), priceTier);

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

        priceTierProperties.setName("testPriceTier_" + RandomFactory.getRandomStringOfAlphabet(10));
        locales.put(defaultLocale, priceTierProperties);
        priceTier.setLocales(locales);

        priceTier.setPrices(prices);
        verifyExpectedError(priceTier.getId(), priceTier);

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
        verifyExpectedError(priceTier.getId(), priceTier);

    }

    private void verifyExpectedError(String priceTierId, PriceTier priceTier) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            priceTierService.updatePriceTier(priceTierId, priceTier, 400);
            Assert.fail("Put item should fail");
        }
        catch (Exception ex) {
            logger.logInfo("expected exception" + ex);
        }
    }

}
