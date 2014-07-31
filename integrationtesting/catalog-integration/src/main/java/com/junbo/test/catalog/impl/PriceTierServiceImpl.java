/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.catalog.PriceTierService;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.RandomFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 * Time: 3/14/2014
 * The implementation for price tier related APIs
 */
public class PriceTierServiceImpl extends HttpClientBase implements PriceTierService {

    private final String catalogServerURL = ConfigHelper.getSetting("defaultCatalogEndpointV1") + "price-tiers";
    private static PriceTierService instance;
    private boolean isServiceScope = true;

    public static synchronized PriceTierService instance() {
        if (instance == null) {
            instance = new PriceTierServiceImpl();
        }
        return instance;
    }

    private PriceTierServiceImpl() {
        componentType = ComponentType.CATALOGADMIN;
    }

    public PriceTier getPriceTier(String priceTierId) throws Exception {
        return getPriceTier(priceTierId, 200);
    }

    public PriceTier getPriceTier(String priceTierId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + priceTierId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<PriceTier>() {}, responseBody);
    }

    public Results<PriceTier> getPriceTiers(HashMap<String, List<String>> httpPara) throws Exception {
        return getPriceTiers(httpPara, 200);
    }

    public Results<PriceTier> getPriceTiers(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<PriceTier>>() {}, responseBody);
    }

    public PriceTier postDefaultPriceTier() throws Exception {
        final String defaultLocale = "en_US";

        PriceTier priceTier = new PriceTier();
        Map<String, SimpleLocaleProperties> locales = new HashMap<>();
        SimpleLocaleProperties simpleLocaleProperties = new SimpleLocaleProperties();
        Map<String, Map<String, BigDecimal>> prices = new HashMap<>();
        Map<String, BigDecimal> price1 = new HashMap<>();
        Map<String, BigDecimal> price2 = new HashMap<>();

        simpleLocaleProperties.setName("testPriceTier_" + RandomFactory.getRandomStringOfAlphabet(10));
        simpleLocaleProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put(defaultLocale, simpleLocaleProperties);

        price1.put(Currency.USD.name(), new BigDecimal(9.83));
        price1.put(Currency.JPY.name(), new BigDecimal(660));

        price2.put(Currency.CNY.name(), new BigDecimal(50.00));
        price2.put(Currency.USD.name(), new BigDecimal(9.55));

        prices.put(Country.US.toString(), price1);
        prices.put(Country.CN.toString(), price2);

        priceTier.setLocales(locales);
        priceTier.setPrices(prices);

        return postPriceTier(priceTier, 200);
    }

    public PriceTier postPriceTier(PriceTier priceTier) throws Exception {
        return postPriceTier(priceTier, 200);
    }

    public PriceTier postPriceTier(PriceTier priceTier, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, priceTier, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<PriceTier>() {}, responseBody);
    }

    public PriceTier updatePriceTier(String priceTierId, PriceTier priceTier) throws Exception {
        return updatePriceTier(priceTierId, priceTier, 200);
    }

    public PriceTier updatePriceTier(String priceTierId, PriceTier priceTier, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + priceTierId;
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, priceTier, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<PriceTier>() {}, responseBody);
    }

    public void deletePriceTier(String priceTierId) throws Exception {
        deletePriceTier(priceTierId, 204);
    }

    public void deletePriceTier(String priceTierId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + priceTierId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode, isServiceScope);
    }

}
