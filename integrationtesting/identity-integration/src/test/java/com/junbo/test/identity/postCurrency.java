/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.model.CurrencyLocaleKey;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postCurrency {

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
            description = "Test Currency POST/PUT/GET",
            environment = "onebox",
            steps = {
                    "1. post a currency" +
                            "/n 2. get the currency" +
                            "/n 3. update the currency"
            }
    )
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

    @Test(groups = "dailies")
    public void testCurrencyGet() throws Exception {
        Currency currency = Identity.CurrencyGetByCurrencyCode("CNY");
        Validator.Validate("Validate Currency Locales not empty", true, currency.getLocales() != null && !currency.getLocales().isEmpty());
        List<String> expectedLocales = new ArrayList<>();
        List<String> unexpectedLocales = new ArrayList<>();
        expectedLocales.add("en_US");
        expectedLocales.add("zh_CN");
        checkCurrencyLocale(currency, expectedLocales, unexpectedLocales);

        currency = Identity.CurrencyGetByCurrencyCode("CNY", "en_US");
        expectedLocales.clear();
        unexpectedLocales.clear();
        expectedLocales.add("en_US");
        unexpectedLocales.add("zh_CN");
        checkCurrencyLocale(currency, expectedLocales, unexpectedLocales);
        Validator.Validate("Validate currency accuracy valid", currency.getLocaleAccuracy(), "HIGH");

        currency = Identity.CurrencyGetByCurrencyCode("CNY", "zh_CN");
        expectedLocales.clear();
        unexpectedLocales.clear();
        expectedLocales.add("zh_CN");
        unexpectedLocales.add("en_US");
        checkCurrencyLocale(currency, expectedLocales, unexpectedLocales);
        Validator.Validate("Validate currency accuracy valid", currency.getLocaleAccuracy(), "HIGH");

        currency = Identity.CurrencyGetByCurrencyCode("CNY", "ja_JP");
        expectedLocales.clear();
        unexpectedLocales.clear();
        expectedLocales.add("ja_JP");
        unexpectedLocales.add("en_US");
        unexpectedLocales.add("zh_CN");
        checkCurrencyLocale(currency, expectedLocales, unexpectedLocales);
        Validator.Validate("Validate currency accuracy valid", currency.getLocaleAccuracy(), "LOW");
    }

    private void checkCurrencyLocale(Currency currency, List<String> expectedLocales, List<String> unexpectedLocales) throws Exception {
        for (String expectedLocale : expectedLocales) {
            CurrencyLocaleKey currencyLocaleKey = currency.getLocales().get(expectedLocale);
            Validator.Validate("Validate " + expectedLocale + " has currency locale", true, currencyLocaleKey != null);
        }

        for (String unexpectedLocale : unexpectedLocales) {
            CurrencyLocaleKey currencyLocaleKey = currency.getLocales().get(unexpectedLocale);
            Validator.Validate("Validate " + unexpectedLocale + " has no currency locale", true, currencyLocaleKey == null);
        }
    }
}
