/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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
        String password = IdentityModel.DefaultPassword();
        Identity.UserCredentialPostDefault(user.getId(), null, password);
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
        UserLoginName loginName = (UserLoginName)JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(loginName.getUserName(), password);
        Validator.Validate("validate response error code", 201, response.getStatusLine().getStatusCode());
    }

    @Test(groups = "dailies")
    public void postUserCredentitalAttemptsMaxRetrySameUser() throws Exception {
        User user = Identity.UserPostDefault();
        String password = IdentityModel.DefaultPassword();
        Identity.UserCredentialPostDefault(user.getId(), null, password);

        String newPassword = IdentityModel.DefaultPassword();
        for (int i = 0; i < 3; i++) {
            UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
            UserLoginName loginName = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserLoginName.class);
            CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                    loginName.getUserName(), newPassword, false);
            Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
            String errorMessage = "User Password Incorrect";
            Validator.Validate("validate response error message", true,
                    EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
            response.close();
        }

        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
        UserLoginName loginName = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                loginName.getUserName(), newPassword, false);
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
                    RandomHelper.randomAlphabetic(15),  IdentityModel.DefaultPassword(), ip, false);
            if (i < 100) {
                Validator.Validate("validate response error code", 404, response.getStatusLine().getStatusCode());
            }
            response.close();
        }
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                RandomHelper.randomAlphabetic(15),  IdentityModel.DefaultPassword(), ip, false);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum login attempt";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }


    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-436
    // We will force a password change for a username change
    public void testPasswordNotInUsername() throws Exception {
        User postedUser = Identity.UserPostDefault(5);
        String password = RandomHelper.randomAlphabetic(3).toLowerCase() +
                RandomHelper.randomNumeric(3) +
                RandomHelper.randomAlphabetic(3).toUpperCase();
        Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(postedUser.getUsername());
        UserLoginName loginName = (UserLoginName)JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(loginName.getUserName(), password);
        Validator.Validate("validate response error code", 201, response.getStatusLine().getStatusCode());
        response.close();

        password = password + loginName.getUserName();
        String url = Identity.IdentityV1UserURI + "/" + Identity.GetHexLongId(postedUser.getId().getValue()) + "/change-credentials";
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        UserCredential userCredential = IdentityModel.DefaultUserCredential(postedUser.getId(), password);
        response = HttpclientHelper.PureHttpResponse(url, JsonHelper.JsonSerializer(userCredential), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "contain username";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        password = IdentityModel.DefaultPassword();
        response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        response.close();
        response = Identity.UserCredentialAttemptesPostDefault(loginName.getUserName(), password);
        response.close();

        // change username, all credential should be invalid.
        userPersonalInfo = new UserPersonalInfo();
        loginName.setUserName(RandomHelper.randomAlphabetic(5));
        userPersonalInfo.setUserId(postedUser.getId());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(loginName));
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.USERNAME.toString());
        userPersonalInfo = Identity.UserPersonalInfoPost(postedUser.getId(), userPersonalInfo);
        postedUser.setUsername(userPersonalInfo.getId());
        User updatedUser = Identity.UserPut(postedUser);
        UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(loginName.getUserName(), password);
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserCredentialAttemptsURI,
                JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
        errorMessage = "User Password Incorrect";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));

        password = IdentityModel.DefaultPassword();
        response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        response.close();
        response = Identity.UserCredentialAttemptesPostDefault(loginName.getUserName(), password);
        response.close();
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-436
    // If the attempt fails three times in succession, it will be lockout.
    // The only way is to reset password
    public void testPasswordRetry() throws Exception {
        User postedUser = Identity.UserPostDefault();
        String password = IdentityModel.DefaultPassword();
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(postedUser.getUsername());
        UserLoginName loginName = (UserLoginName)JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(loginName.getUserName(), password);
        response.close();

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        for (int i = 0; i < 4; i++) {
            UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(loginName.getUserName(), IdentityModel.DefaultPassword());
            response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserCredentialAttemptsURI,
                    JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
            if (i < 3) {
                Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
                String errorMessage = "User Password Incorrect";
                Validator.Validate("validate response error message", true,
                    EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
            }
            response.close();
        }

        UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(loginName.getUserName(), password);
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserCredentialAttemptsURI,
                JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum allowed retry count";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));

        response.close();

        password = IdentityModel.DefaultPassword();
        response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(loginName.getUserName(), password, true);
        response.close();
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-436
    // If the attempt fails three times not in succession, it still can be used.
    public void testPasswordNotSuccessionRetry() throws Exception {
        User postedUser = Identity.UserPostDefault();
        String password = IdentityModel.DefaultPassword();
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(postedUser.getUsername());
        UserLoginName loginName = (UserLoginName)JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password, true);
        response.close();

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        for (int i = 0; i < 2; i++) {
            UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(loginName.getUserName(), IdentityModel.DefaultPassword());
            response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserCredentialAttemptsURI,
                    JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
            if (i < 2) {
                Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
                String errorMessage = "User Password Incorrect";
                Validator.Validate("validate response error message", true,
                        EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
            }
            response.close();
        }

        response = Identity.UserCredentialAttemptesPostDefault(loginName.getUserName(), password, true);
        response.close();

        for (int i = 0; i < 2; i++) {
            UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(loginName.getUserName(), IdentityModel.DefaultPassword());
            response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserCredentialAttemptsURI,
                    JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
            if (i < 2) {
                Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
                String errorMessage = "User Password Incorrect";
                Validator.Validate("validate response error message", true,
                        EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
            }
            response.close();
        }

        response = Identity.UserCredentialAttemptesPostDefault(loginName.getUserName(), password, true);
        response.close();
    }
}
