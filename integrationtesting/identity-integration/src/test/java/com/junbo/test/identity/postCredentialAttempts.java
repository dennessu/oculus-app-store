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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postCredentialAttempts {

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
    public void postUserCredentitalAttempts() throws Exception {
        String email = RandomHelper.randomEmail();
        User user = Identity.UserPostDefaultWithMail(15, email);
        String password = IdentityModel.DefaultPassword();
        Identity.UserCredentialPostDefault(user.getId(), null, password);
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
        UserLoginName loginName = (UserLoginName) JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(email, password);
        Validator.Validate("validate response error code", 201, response.getStatusLine().getStatusCode());
    }

    @Test(groups = "dailies")
    public void postUserCredentitalAttemptsMaxRetrySameUser() throws Exception {
        String email = RandomHelper.randomEmail();
        User user = Identity.UserPostDefaultWithMail(15, email);
        String password = IdentityModel.DefaultPassword();
        Identity.UserCredentialPostDefault(user.getId(), null, password);

        String newPassword = IdentityModel.DefaultPassword();
        for (int i = 0; i < 5; i++) {
            UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
            UserLoginName loginName = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserLoginName.class);
            CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                    email, newPassword, false);
            Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
            String errorMessage = "User Password Incorrect";
            Validator.Validate("validate response error message", true,
                    EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
            response.close();
        }

        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
        UserLoginName loginName = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                email, newPassword, false);
        Validator.Validate("validate response error code", 429, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum login attempts";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }

    @Test(groups = "dailies")
    public void postUserCredentitalAttemptsMaxRetrySameIP() throws Exception {
        String ip = RandomHelper.randomIP();
        for (int i = 0; i < 101; i++) {
            CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                    RandomHelper.randomEmail(), IdentityModel.DefaultPassword(), ip, false);
            if (i < 100) {
                Validator.Validate("validate response error code", 404, response.getStatusLine().getStatusCode());
            }
            response.close();
        }
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                RandomHelper.randomEmail(), IdentityModel.DefaultPassword(), ip, false);
        Validator.Validate("validate response error code", 429, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum login attempts";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        ip = "0.0.0.0";
        for (int i = 0; i<101; i++) {
            response = Identity.UserCredentialAttemptesPostDefault(
                    RandomHelper.randomEmail(), IdentityModel.DefaultPassword(), ip, false);
            if (i < 100) {
                Validator.Validate("validate response error code", 404, response.getStatusLine().getStatusCode());
            }
            response.close();
        }

        response = Identity.UserCredentialAttemptesPostDefault(
                RandomHelper.randomEmail(), IdentityModel.DefaultPassword(), ip, false);
        errorMessage = "User Name Not Found";
        Validator.Validate("validate response error code", 404, response.getStatusLine().getStatusCode());
        Validator.Validate("validate response error message", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }

    @Test(groups = "dailies")
    public void postUserCredentialAttempsExistingUserMaxRetrySameIP() throws Exception {
        String ip = RandomHelper.randomIP();
        String email = RandomHelper.randomEmail();
        User user = Identity.UserPostDefaultWithMail(15, email);
        String password = RandomHelper.randomAlphabetic(10);
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(user.getId(), null, password);
        response.close();

        for (int i = 0; i < 105; i++) {
            email = RandomHelper.randomEmail();
            user = Identity.UserPostDefaultWithMail(15, email);
            response = Identity.UserCredentialPostDefault(user.getId(), null, password);
            response.close();
            boolean flag = (i < 100);
            response = Identity.UserCredentialAttemptesPostDefault(email, password, ip, flag);
            response.close();
        }
        response = Identity.UserCredentialAttemptesPostDefault(email, password, ip, false);
        Validator.Validate("validate response error code", 429, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum login attempts";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        ip = "0.0.0.0";
        for (int i = 0; i < 200; i++) {
            email = RandomHelper.randomEmail();
            user = Identity.UserPostDefaultWithMail(15, email);
            response = Identity.UserCredentialPostDefault(user.getId(), null, password);
            response.close();
            response = Identity.UserCredentialAttemptesPostDefault(email, password, ip, true);
            response.close();
        }
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-436
    // We will force a password change for a username change
    public void testPasswordNotInUsername() throws Exception {
        String email = RandomHelper.randomEmail();
        User postedUser = Identity.UserPostDefaultWithMail(5, email);
        String password = RandomHelper.randomAlphabetic(3).toLowerCase() +
                RandomHelper.randomNumeric(3) +
                RandomHelper.randomAlphabetic(3).toUpperCase();
        Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(postedUser.getUsername());
        UserLoginName loginName = (UserLoginName) JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(email, password);
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
        response = Identity.UserCredentialAttemptesPostDefault(email, password);
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
        UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(email, password);
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserCredentialAttemptsURI,
                JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
        errorMessage = "User Password Incorrect";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));

        password = IdentityModel.DefaultPassword();
        response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        response.close();
        response = Identity.UserCredentialAttemptesPostDefault(email, password);
        response.close();
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-436
    // If the attempt fails three times in succession, it will be lockout.
    // The only way is to reset password
    public void testPasswordRetry() throws Exception {
        String email = RandomHelper.randomEmail();
        User postedUser = Identity.UserPostDefaultWithMail(15, email);
        String password = IdentityModel.DefaultPassword();
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(postedUser.getUsername());
        UserLoginName loginName = (UserLoginName) JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(email, password);
        response.close();

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        for (int i = 0; i < 6; i++) {
            UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(email, IdentityModel.DefaultPassword());
            response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserCredentialAttemptsURI,
                    JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
            if (i < 5) {
                Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
                String errorMessage = "User Password Incorrect";
                Validator.Validate("validate response error message", true,
                        EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
            }
            response.close();
        }

        UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(email, password);
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserCredentialAttemptsURI,
                JsonHelper.JsonSerializer(ucva), HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("validate response error code", 429, response.getStatusLine().getStatusCode());
        String errorMessage = "User reaches maximum login attempts";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));

        response.close();
        Thread.sleep(1000);

        Thread.sleep(2000);
        password = IdentityModel.DefaultPassword();
        response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password);
        response.close();

        Boolean success = false;
        for (int i = 0; i < 12; i++) {
            Thread.sleep(i * 2 * 100);
            response = Identity.UserCredentialAttemptesPostDefault(email, password, false);
            if (response.getStatusLine().getStatusCode() == 201) {
                success = true;
                break;
            }
            response.close();
        }
        Validator.Validate("validate login finally succeeded", true, success);
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-436
    // If the attempt fails three times not in succession, it still can be used.
    public void testPasswordNotSuccessionRetry() throws Exception {
        String email = RandomHelper.randomEmail();
        User postedUser = Identity.UserPostDefaultWithMail(15, email);
        String password = IdentityModel.DefaultPassword();
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(postedUser.getUsername());
        UserLoginName loginName = (UserLoginName) JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(postedUser.getId(), null, password, true);
        response.close();

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        for (int i = 0; i < 2; i++) {
            UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(email, IdentityModel.DefaultPassword());
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

        response = Identity.UserCredentialAttemptesPostDefault(email, password, true);
        response.close();

        for (int i = 0; i < 2; i++) {
            UserCredentialVerifyAttempt ucva = IdentityModel.DefaultUserCredentialAttempts(email, IdentityModel.DefaultPassword());
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

        response = Identity.UserCredentialAttemptesPostDefault(email, password, true);
        response.close();
    }

    @Test(groups = "dailies")
    public void testUserPinAttempts() throws Exception {
        String email = RandomHelper.randomEmail();
        User user = Identity.UserPostDefaultWithMail(15, email);
        String password = IdentityModel.DefaultPassword();
        Identity.UserCredentialPostDefault(user.getId(), null, password);
        UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
        UserLoginName loginName = (UserLoginName) JsonHelper.JsonNodeToObject(userPersonalInfo.getValue(), UserLoginName.class);
        CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(email, password);
        Validator.Validate("validate response error code", 201, response.getStatusLine().getStatusCode());
        response.close();

        String pin = IdentityModel.DefaultPin();
        response = Identity.UserPinCredentialPostDefault(user.getId(), password, pin, true);
        response.close();
        response = Identity.UserPinCredentialAttemptPostDefault(email, pin);
        response.close();

        pin = IdentityModel.DefaultPin();
        response = Identity.UserPinCredentialPostDefault(user.getId(), null, pin, true);
        response.close();
        response = Identity.UserPinCredentialAttemptPostDefault(email, pin);
        response.close();

        pin = IdentityModel.DefaultPin();
        response = Identity.UserPinCredentialPostDefault(user.getId(), password, pin, true);
        response.close();
        response = Identity.UserPinCredentialAttemptPostDefault(email, pin);
        response.close();
    }

    @Test(groups = "dailies")
    public void testUserCredentialAttemptsRetryMaximumMail() throws Exception{
        User user = Identity.UserPostDefaultWithMail(15, "xia.wayne2+" + RandomHelper.randomAlphabetic(15) + "@gmail.com");
        String password = IdentityModel.DefaultPassword();
        Identity.UserCredentialPostDefault(user.getId(), null, password);

        String newPassword = IdentityModel.DefaultPassword();

        for (int i = 0; i < 5; i++) {
            UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
            UserLoginName loginName = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserLoginName.class);
            CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                    loginName.getUserName(), newPassword, false);
            response.close();
        }
    }

    @Test(groups = "dailies")
    public void testUserCredentialAttemptsWithoutMail() throws Exception {
        String email = RandomHelper.randomEmail();
        User user = Identity.UserPostDefaultWithMail(15, email);
        String password = IdentityModel.DefaultPassword();
        Identity.UserCredentialPostDefault(user.getId(), null, password);
        String newPassword = IdentityModel.DefaultPassword();

        for (int i = 0; i < 5; i++) {
            UserPersonalInfo userPersonalInfo = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
            UserLoginName loginName = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.getValue(), UserLoginName.class);
            CloseableHttpResponse response = Identity.UserCredentialAttemptesPostDefault(
                    email, newPassword, false);
            Validator.Validate("validate response error code", 412, response.getStatusLine().getStatusCode());
            String errorMessage = "User Password Incorrect";
            Validator.Validate("validate response error message", true,
                    EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
            response.close();
        }
    }
}
