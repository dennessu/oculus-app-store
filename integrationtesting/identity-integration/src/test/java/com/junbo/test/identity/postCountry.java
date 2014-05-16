/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Country;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author dw
 */
public class postCountry {

    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postCountry() throws Exception {
        Country posted = Identity.CountryPostDefault();
        try {
            Country stored = Identity.CountryGetByCountryId(posted.getId().getValue());
            assertEquals("validate country code is correct",
                    posted.getCountryCode(), stored.getCountryCode());
            assertEquals("validate country locales is correct",
                    posted.getLocales(), stored.getLocales());
            assertEquals("validate country default currency is correct",
                    posted.getDefaultCurrency(), stored.getDefaultCurrency());
            assertEquals("validate country default locale is correct",
                    posted.getDefaultLocale(), stored.getDefaultLocale());
            assertEquals("validate country rating board id is correct",
                    posted.getRatingBoardId(), stored.getRatingBoardId());
            assertEquals("validate country sub countries is correct",
                    JsonHelper.ObjectToJsonNode(posted.getSubCountries()),
                    JsonHelper.ObjectToJsonNode(stored.getSubCountries()));
            assertEquals("validate country supported locales is correct",
                    JsonHelper.ObjectToJsonNode(posted.getSupportedLocales()),
                    JsonHelper.ObjectToJsonNode(stored.getSupportedLocales()));
        } finally {
            Identity.CountryDeleteByCountryId(posted.getId().getValue());
        }
    }
}
