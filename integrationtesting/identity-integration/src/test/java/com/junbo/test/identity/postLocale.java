/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.test.common.HttpclientHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author dw
 */
public class postLocale {

    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postLocale() throws Exception {
        Locale posted = Identity.LocalePostDefault();
        try {
            Locale stored = Identity.LocaleGetByLocaleId(posted.getId().getValue());
            assertEquals("validate locale code is correct",
                    posted.getLocaleCode(), stored.getLocaleCode());
            assertEquals("validate locale name is correct",
                    posted.getLocaleName(), stored.getLocaleName());
            assertEquals("validate locale long name is correct",
                    posted.getLongName(), stored.getLongName());
            assertEquals("validate locale short name is correct",
                    posted.getShortName(), stored.getShortName());
        } finally {
            Identity.LocaleDeleteByLocaleId(posted.getId().getValue());
        }
    }
}
