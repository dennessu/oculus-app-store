/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Country;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

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
        try {
            Identity.LocalePostDefault();
            Identity.CurrencyPostDefault();
            Country posted = Identity.CountryPostDefault();
            Country stored = Identity.CountryGetByCountryId(posted.getId().getValue());
            Validator.Validate("validate country", posted, stored);
        } finally {
            Identity.CountryDeleteByCountryId(IdentityModel.DefaultCountry);
            Identity.CurrencyDeleteByCurrencyCode(IdentityModel.DefaultCurrency);
            Identity.LocaleDeleteByLocaleId(IdentityModel.DefaultLocale);
        }
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
}
