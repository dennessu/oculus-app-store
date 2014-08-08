/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * @author dw
 */
public class postCredential {

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
    public void postUserCredentital() throws Exception {
        User user = Identity.UserPostDefault();
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(user.getId(),
                RandomHelper.randomAlphabetic(4).toUpperCase() +
                        Math.abs(RandomHelper.randomInt()) % 1000 +
                        RandomHelper.randomAlphabetic(4).toLowerCase());
        Validator.Validate("validate response error code", 201, response.getStatusLine().getStatusCode());
    }
}
