/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author dw
 */
public class postCurrency {

    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postCurrency() throws Exception {
        Currency posted = Identity.CurrencyPostDefault();
        try {
            Currency stored = Identity.CurrencyGetByCurrencyCode(posted.getCurrencyCode());
            assertEquals("validate currency code is correct",
                    posted.getCurrencyCode(), stored.getCurrencyCode());
            assertEquals("validate currency decimal symbol is correct",
                    posted.getDecimalSymbol(), stored.getDecimalSymbol());
            assertEquals("validate currency digit grouping symbol is correct",
                    posted.getDigitGroupingSymbol(), stored.getDecimalSymbol());
            assertEquals("validate currency negative format is correct",
                    posted.getNegativeFormat(), stored.getNegativeFormat());
            assertEquals("validate currency symbol is correct",
                    posted.getSymbol(), stored.getSymbol());
            assertEquals("validate currency symbol position is correct",
                    posted.getSymbolPosition(), stored.getSymbolPosition());
            assertEquals("validate currency countries is correct",
                    JsonHelper.ObjectToJsonNode(posted.getCountries()),
                    JsonHelper.ObjectToJsonNode(stored.getCountries()));
            assertEquals("validate currency digit grouping length is correct",
                    posted.getDigitGroupingLength(), stored.getDigitGroupingLength());
            assertEquals("validate currency locales is correct",
                    JsonHelper.ObjectToJsonNode(posted.getLocales()),
                    JsonHelper.ObjectToJsonNode(stored.getLocales()));
            assertEquals("validate currency number after decimal is correct",
                    posted.getNumberAfterDecimal(), stored.getNumberAfterDecimal());
        } finally {
            Identity.CurrencyDeleteByCurrencyCode(posted.getCurrencyCode());
        }
    }
}
