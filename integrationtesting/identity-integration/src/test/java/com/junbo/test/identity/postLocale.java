/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.commons.collections.CollectionUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

    @Property(
            priority = Priority.BVT,
            component = Component.Identity,
            owner = "JieFeng",
            status = Status.Enable,
            description = "Test Locale POST/PUT/GET",
            environment = "onebox",
            steps = {
                    "1. post a locale" +
                 "/n 2. get the locale" +
                 "/n 3. update the locale"
            }
    )
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

        Results<Locale> results = Identity.LocaleGetAll();
        Validator.Validate("validate locale getAll", true, !CollectionUtils.isEmpty(results.getItems()));
    }
}
