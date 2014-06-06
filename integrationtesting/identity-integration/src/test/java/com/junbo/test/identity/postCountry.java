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
}
