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
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author dw
 */
public class postCredentialAttempts {

    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postUserCredentitalAttempts() throws Exception {
        User user = Identity.UserPostDefault();
        String password = RandomHelper.randomNumeric(6) + RandomHelper.randomAlphabetic(6);
        Identity.UserCredentialPostDefault(user.getId(), password);
        Identity.UserCredentialAttemptesPostDefault(user.getUsername(), password);
    }

    @Test(groups = "dailies")
    public void postUserCredentitalAttemptsMaxRetry() throws Exception {
        User user = Identity.UserPostDefault();
        String password = RandomHelper.randomNumeric(6) + RandomHelper.randomAlphabetic(6);
        Identity.UserCredentialPostDefault(user.getId(), password);

        String newPassword = RandomHelper.randomNumeric(6) + RandomHelper.randomAlphabetic(6);
        for (int i = 0; i < 3; i++) {
            CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                    user.getUsername(), newPassword, false);
            Validator.Validate("validate response error code", 404, response.getStatusLine().getStatusCode());
            String errorMessage = "User password is incorrect.";
            Validator.Validate("validate response error message", true,
                    EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        }

        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                user.getUsername(), newPassword, false);
        Validator.Validate("validate response error code", 409, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum allowed retry count";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
    }

}