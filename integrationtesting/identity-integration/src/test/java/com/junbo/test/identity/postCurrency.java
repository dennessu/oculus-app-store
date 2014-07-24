/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * @author dw
 */
public class postCurrency {

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
    public void postCurrency() throws Exception {
        Identity.CurrencyDeleteByCurrencyCode(IdentityModel.DefaultCurrency);
        Currency currency = IdentityModel.DefaultCurrency();
        Currency posted = Identity.CurrencyPostDefault(currency);
        Currency stored = Identity.CurrencyGetByCurrencyCode(posted.getCurrencyCode());
        Validator.Validate("validate currency code", currency.getCurrencyCode(), stored.getCurrencyCode());
        Validator.Validate("validate currency locales", currency.getLocales(), stored.getLocales());
        Validator.Validate("validate currency symbol", currency.getSymbol(), stored.getSymbol());
        Validator.Validate("validate currency min auth amount",
                currency.getMinAuthAmount(), stored.getMinAuthAmount());
        Validator.Validate("validate currency number after decimal",
                currency.getNumberAfterDecimal(), stored.getNumberAfterDecimal());
        Validator.Validate("validate currency symbol position",
                currency.getSymbolPosition(), stored.getSymbolPosition());
    }
}
