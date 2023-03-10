/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserCredential;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postCredential {

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

    @Test(groups = "bvt")
    public void postUserCredentital() throws Exception {
        User user = Identity.UserPostDefault();
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(user.getId(), null,
                RandomHelper.randomAlphabetic(4).toUpperCase() +
                        Math.abs(RandomHelper.randomInt()) % 1000 +
                        RandomHelper.randomAlphabetic(4).toLowerCase());
        Validator.Validate("validate response error code", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserPinCredentialPostDefault(user.getId(), null, IdentityModel.DefaultPin(), true);
        response.close();
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-436
    // Remediation
    // Require all passwords to contain at least three of the four following character classes:
    // Lower case characters
    // Upper case characters
    // Number
    // "Special" characters (e.g. . @#$%^&*()_+|~-=\`{}[]:";'<>/ etc)
    // Require a minimum password length to 8 characters.
    public void testCredentialValid() throws Exception {
        User user = Identity.UserPostDefault();
        String password = RandomHelper.randomAlphabetic(6);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        UserCredential uc = IdentityModel.DefaultUserCredential(user.getId(), password);
        CloseableHttpResponse response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1UserURI + "/" + Identity.GetHexLongId(user.getId().getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(uc), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response code", 412, response.getStatusLine().getStatusCode());
        String errorMessage = "Password length should be larger than 8";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        password = RandomHelper.randomNumeric(6);
        uc = IdentityModel.DefaultUserCredential(user.getId(), password);
        response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1UserURI + "/" + Identity.GetHexLongId(user.getId().getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(uc), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response code", 412, response.getStatusLine().getStatusCode());
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        password = RandomHelper.randomNumeric(126);
        uc = IdentityModel.DefaultUserCredential(user.getId(), password);
        response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1UserURI + "/" + Identity.GetHexLongId(user.getId().getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(uc), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        response.close();

        password = RandomHelper.randomNumeric(8);
        uc = IdentityModel.DefaultUserCredential(user.getId(), password);
        response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1UserURI + "/" + Identity.GetHexLongId(user.getId().getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(uc), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        response.close();

        password = RandomHelper.randomAlphabetic(9) + " " + RandomHelper.randomNumeric(5) + "+" + RandomHelper.randomAlphabetic(4);
        uc = IdentityModel.DefaultUserCredential(user.getId(), password);
        response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1UserURI + "/" + Identity.GetHexLongId(user.getId().getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(uc), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response code", 201, response.getStatusLine().getStatusCode());
        response.close();
    }

    @Test(groups = "dailies")
    public void testCredentialUpdate() throws Exception {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));

        User user = Identity.UserPostDefault();
        String password = IdentityModel.DefaultPassword();
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(user.getId(), null, password);
        response.close();

        String oldPassword = password;
        String newPassword = IdentityModel.DefaultPassword();
        response = Identity.UserCredentialPostDefault(user.getId(), oldPassword, newPassword);
        response.close();

        String randomPassword = IdentityModel.DefaultPassword();
        UserCredential uc = IdentityModel.DefaultUserCredential(user.getId(), randomPassword, newPassword);

        response = HttpclientHelper.GetHttpResponse(
                Identity.IdentityV1UserURI + "/" + Identity.GetHexLongId(user.getId().getValue()) + "/change-credentials",
                JsonHelper.JsonSerializer(uc), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response code", 412, response.getStatusLine().getStatusCode());

        String errorMessage = "User Password Incorrect";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        Identity.UserCredentialPostDefault(user.getId(), newPassword, randomPassword);
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-463
    public void testInvalidPin() throws Exception {
        User user = Identity.UserPostDefault();
        String password = IdentityModel.DefaultPassword();
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(user.getId(), null, password);
        Validator.Validate("validate response error code", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserPinCredentialPostDefault(user.getId(), null, RandomHelper.randomNumeric(10), false);
        Validator.Validate("Validate pin post response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "Invalid Pin Pattern";
        Validator.Validate("Validate pin post response error Message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        response = Identity.UserPinCredentialPostDefault(user.getId(), null, IdentityModel.DefaultPin(), true);
        response.close();

        response = Identity.UserPinCredentialPostDefault(user.getId(), IdentityModel.DefaultPassword(), IdentityModel.DefaultPin(), false);
        Validator.Validate("Validate pin change response error code", 412, response.getStatusLine().getStatusCode());
        errorMessage = "User Password Incorrect";
        Validator.Validate("Validate pin change response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        response = Identity.UserPinCredentialPostDefault(user.getId(), password, IdentityModel.DefaultPin(), true);
        response.close();

        response = Identity.UserPinCredentialPostDefault(user.getId(), password, RandomHelper.randomNumeric(10), false);
        Validator.Validate("Validate pin post response error code", 400, response.getStatusLine().getStatusCode());
        errorMessage = "Invalid Pin Pattern";
        Validator.Validate("Validate pin post response error Message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }
}