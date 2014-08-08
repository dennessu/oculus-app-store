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
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * @author dw
 */
public class postCredentialAttempts {

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
    public void postUserCredentitalAttempts() throws Exception {
        User user = Identity.UserPostDefault();
        String password = RandomHelper.randomNumeric(6) + RandomHelper.randomAlphabetic(6);
        Identity.UserCredentialPostDefault(user.getId(), password);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(user.getUsername(), password);
        Validator.Validate("validate response error code", 201, response.getStatusLine().getStatusCode());
    }

    @Test(groups = "dailies")
    public void postUserCredentitalAttemptsMaxRetrySameUser() throws Exception {
        User user = Identity.UserPostDefault();
        String password = RandomHelper.randomAlphabetic(4).toLowerCase() +
                RandomHelper.randomNumeric(6) +
                RandomHelper.randomAlphabetic(4).toUpperCase();
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(user.getId(), password);
        response.close();

        String wrongPassword = RandomHelper.randomAlphabetic(4).toLowerCase() +
                RandomHelper.randomNumeric(6) +
                RandomHelper.randomAlphabetic(4).toUpperCase();
        response = Identity.UserCredentialPostDefault(user.getId(), password);
        response.close();
        for (int i = 0; i < 1; i++) {
            response = Identity.UserCredentialAttemptesPostDefault(
                    user.getUsername(), wrongPassword, false);
            Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
            String errorMessage = "User Password Incorrect";
            Validator.Validate("validate response error message", true,
                    EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
            response.close();
        }

        response = Identity.UserCredentialAttemptesPostDefault(
                user.getUsername(), wrongPassword, false);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum allowed retry count";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }

    @Test(groups = "dailies")
    public void postUserCredentitalAttemptsMaxRetrySameIP() throws Exception {
        String ip = RandomHelper.randomIP();
        for (int i = 0; i < 101; i++) {
            CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                    RandomHelper.randomAlphabetic(15), RandomHelper.randomAlphabetic(15), ip, false);
            if (i < 100) {
                Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
            }
            response.close();
        }
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                RandomHelper.randomAlphabetic(15), RandomHelper.randomAlphabetic(15), ip, false);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum login attempt";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }

}
