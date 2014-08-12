/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Country;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.Validator;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postCountry {

    @BeforeSuite
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
        Validator.Validate("validate response error code", 404, response.getStatusLine().getStatusCode());
    }
}
