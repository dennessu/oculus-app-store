/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.*;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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

    @BeforeClass(alwaysRun = true)
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeader();
        HttpclientHelper.CloseHttpClient();
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod(alwaysRun = true)
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

    @Test(groups = "dailies")
    public void searchCountryByLocale() throws Exception {

        String invalidSortByURL = Identity.IdentityV1CountryURI + "?locale=en_US&sortBy=locale";
        String missingLocaleByURL = Identity.IdentityV1CountryURI + "?sortBy=shortName";

        List<Country> countries = Identity.CountriesSearch("ab_CD", "shortName");
        assert countries != null;
        for (Country country : countries) {
            Validator.Validate("Validate " + country.getCountryCode(), country.getSubCountries() != null, true);
            CountryLocaleKey countryLocaleKey = country.getLocales().get("ab_CD");
            Validator.Validate("Validate shortName ", countryLocaleKey.getShortName() == null, true);
            Validator.Validate("Validate LongName ", countryLocaleKey.getShortName() == null, true);
            Validator.Validate("Validate postCode", countryLocaleKey.getPostalCode() == null, true);
        }

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(invalidSortByURL, null, HttpclientHelper.HttpRequestType.get, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "Query parameter is invalid";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        errorMessage = "Query parameter is required";
        response = HttpclientHelper.PureHttpResponse(missingLocaleByURL, null, HttpclientHelper.HttpRequestType.get, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        countries = Identity.CountriesSearch("en_US", "shortName");
        assert countries != null;
        int chinaIndex = 0, usIndex = 0;
        for (int index = 0; index < countries.size(); index ++) {
            Country country = countries.get(index);
            if (country.getCountryCode().equalsIgnoreCase("US")) {
                usIndex = index;
            } else if (country.getCountryCode().equalsIgnoreCase("CN")) {
                chinaIndex = index;
            }
        }
        assert chinaIndex < usIndex;

        chinaIndex = usIndex = 0;
        countries = Identity.CountriesSearch("zh_CN", "shortName");
        assert countries != null;
        for (int index = 0; index < countries.size(); index ++) {
            Country country = countries.get(index);
            if (country.getCountryCode().equalsIgnoreCase("US")) {
                usIndex = index;
            } else if (country.getCountryCode().equalsIgnoreCase("CN")) {
                chinaIndex = index;
            }
        }
        assert chinaIndex > usIndex;
    }

    @Test(groups = "dailies")
    // todo:    Need jason fix this.
    public void searchCountriesWithValidLocale() throws Exception {
        List<Locale> locales = Identity.LocaleGetAll().getItems();

        for (Locale locale : locales) {
            List<Country> countries = Identity.CountriesSearch(locale.getLocaleCode(), "shortName");
            for (Country country : countries) {
                Validator.Validate("Validate " + country.getCountryCode(), country.getSubCountries() != null, true);
                CountryLocaleKey countryLocaleKey = country.getLocales().get(locale.getLocaleCode());
                Validator.Validate("Validate shortName ", countryLocaleKey.getShortName() != null, true);
                Validator.Validate("Validate LongName ", countryLocaleKey.getShortName() != null, true);
                Validator.Validate("Validate postCode", countryLocaleKey.getPostalCode() != null, true);
            }
        }
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
