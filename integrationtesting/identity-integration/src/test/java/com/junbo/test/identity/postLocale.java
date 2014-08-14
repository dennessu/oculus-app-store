/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.*;

/**
 * @author dw
 */
public class postLocale {

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

    @Test(groups = "bvt")
    public void postLocale() throws Exception {
        Identity.LocaleDeleteByLocaleId(IdentityModel.DefaultLocale);
        Locale locale = IdentityModel.DefaultLocale();
        Locale posted = Identity.LocalePostDefault(locale);
        Locale stored = Identity.LocaleGetByLocaleId(posted.getId().getValue());
        Validator.Validate("validate locale code", locale.getLocaleCode(), stored.getLocaleCode());
        Validator.Validate("validate locale name", locale.getLocaleName(), stored.getLocaleName());
        Validator.Validate("validate locale long name", locale.getLongName(), stored.getLongName());
        Validator.Validate("validate locale short name", locale.getShortName(), stored.getShortName());
    }
}
