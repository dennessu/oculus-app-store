/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Country;
import com.junbo.identity.spec.v1.model.CountryLocaleKey;
import com.junbo.identity.spec.v1.model.SubCountryLocaleKey;
import com.junbo.identity.spec.v1.model.SubCountryLocaleKeys;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dw
 */
public class postCountry {

    @BeforeClass
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeader();
        HttpclientHelper.CloseHttpClient();
    }

    @BeforeMethod
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Property(
            priority = Priority.BVT,
            component = Component.Identity,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Test country POST/PUT/GET",
            environment = "onebox",
            steps = {
                    "1. post a country" +
                 "/n 2. get the country" +
                 "/n 3. update the country"
            }
    )
    @Test(groups = "bvt")
    public void postCountry() throws Exception {
        Identity.CountryDeleteByCountryId(IdentityModel.DefaultCountry);
        Country country = IdentityModel.DefaultCountry();
        Country posted = Identity.CountryPostDefault(country);
        Country stored = Identity.CountryGetByCountryId(posted.getId().getValue());
        Validator.Validate("validate country code", country.getCountryCode(), stored.getCountryCode());
        Validator.Validate("validate country default currency",
                country.getDefaultCurrency(), stored.getDefaultCurrency());
        Validator.Validate("validate country default locale", country.getDefaultLocale(), stored.getDefaultLocale());
        Validator.Validate("validate country locales", country.getLocales(), stored.getLocales());
        Validator.Validate("validate country rating boards", country.getRatingBoards(), stored.getRatingBoards());
        Validator.Validate("validate country sub countries", country.getSubCountries(), stored.getSubCountries());
        Validator.Validate("validate country supported locales",
                country.getSupportedLocales(), stored.getSupportedLocales());
    }

    @Test(groups = "dailies")
    public void getCountry() throws Exception {
        List<Country> countries = Identity.CountriesGet();
        List<Country> tier1Countries = CountryDomainData.Tier1CountryData();
        for (Country c : tier1Countries) {
            for (Country cc : countries) {
                if (c.getCountryCode() == cc.getCountryCode()) {
                    Validator.Validate("validate currency", c.getDefaultCurrency(), cc.getDefaultCurrency());
                    Validator.Validate("validate locale", c.getDefaultLocale(), cc.getDefaultLocale());
                }
            }
        }
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-456
    public void testWORLDCountryNotExists() throws Exception {
        String url = Identity.IdentityV1CountryURI + "/" + "WORLD";
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(
                url, null, HttpclientHelper.HttpRequestType.get, nvps);
        Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
    }

    @Test(groups = "dailies")
    public void getCountryByLocale() throws Exception {
        Country country = Identity.CountryGetByCountryId("CN");
        Validator.Validate("Validate country subCountries not empty", true, !country.getSubCountries().isEmpty());
        Validator.Validate("Validate country locales not empty", true, !country.getLocales().isEmpty());
        List<String> expectedLocales = new ArrayList<>();
        List<String> unexpectedLocales = new ArrayList<>();
        expectedLocales.add("en_US");
        expectedLocales.add("zh_CN");
        checkCountryLocale(country, expectedLocales, unexpectedLocales);

        country = Identity.CountryGetByCountryId("CN", "en_US");
        expectedLocales.clear();
        unexpectedLocales.clear();
        expectedLocales.add("en_US");
        unexpectedLocales.add("zh_CN");
        checkCountryLocale(country, expectedLocales, unexpectedLocales);
        checkCountryAccuracy(country, "HIGH");

        country = Identity.CountryGetByCountryId("CN", "zh_CN");
        expectedLocales.clear();
        unexpectedLocales.clear();
        expectedLocales.add("zh_CN");
        unexpectedLocales.add("en_US");
        checkCountryLocale(country, expectedLocales, unexpectedLocales);
        checkCountryAccuracy(country, "HIGH");

        country = Identity.CountryGetByCountryId("CN", "ja_JP");
        expectedLocales.clear();
        unexpectedLocales.clear();
        expectedLocales.add("ja_JP");
        unexpectedLocales.add("en_US");
        unexpectedLocales.add("zh_CN");
        checkCountryLocale(country, expectedLocales, unexpectedLocales);
        checkCountryAccuracy(country, "LOW");
    }

    private void checkCountryLocale(Country country, List<String> expectedLocales, List<String> unexpectedLocales) throws Exception {
        Iterator<Map.Entry<String, SubCountryLocaleKeys>> iterator = country.getSubCountries().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, SubCountryLocaleKeys> entry = iterator.next();
            SubCountryLocaleKeys subCountryLocaleKeys = entry.getValue();

            if (!CollectionUtils.isEmpty(expectedLocales)) {
                for (String expectedLocale : expectedLocales) {
                    SubCountryLocaleKey subCountryLocaleKey = subCountryLocaleKeys.getLocales().get(expectedLocale);
                    Validator.Validate("Validate " + expectedLocale + " locale exists", true, subCountryLocaleKey != null);
                }
            }

            if (!CollectionUtils.isEmpty(unexpectedLocales)) {
                for (String unexpectedLocale : unexpectedLocales) {
                    SubCountryLocaleKey subCountryLocaleKey = subCountryLocaleKeys.getLocales().get(unexpectedLocale);
                    Validator.Validate("Validate " + unexpectedLocale + " locale not exists", true, subCountryLocaleKey == null);
                }
            }
        }

        if (!CollectionUtils.isEmpty(expectedLocales)) {
            for (String expectedLocale : expectedLocales) {
                CountryLocaleKey countryLocaleKey = country.getLocales().get(expectedLocale);
                Validator.Validate("Validate " + expectedLocale + " countryLocale exists", true, countryLocaleKey != null);
            }
        }

        if (!CollectionUtils.isEmpty(unexpectedLocales)) {
            for (String unexpectedLocale : unexpectedLocales) {
                CountryLocaleKey countryLocaleKey = country.getLocales().get(unexpectedLocale);
                Validator.Validate("Validate " + unexpectedLocale + " countryLocale not exists", true, countryLocaleKey == null);
            }
        }
    }

    private void checkCountryAccuracy(Country country, String accuracy) throws Exception {
        Iterator<Map.Entry<String, SubCountryLocaleKeys>> iterator = country.getSubCountries().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, SubCountryLocaleKeys> entry = iterator.next();
            SubCountryLocaleKeys subCountryLocaleKeys = entry.getValue();
            Validator.Validate("Validate subCountryLocaleKeys", subCountryLocaleKeys.getLocaleAccuracy(), accuracy);
        }

        Validator.Validate("Validae localeKeys", country.getLocaleAccuracy(), accuracy);
    }
}
