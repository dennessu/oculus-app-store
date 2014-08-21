/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.libs.IdConverter;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dw
 */
public class postUser {

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
    public void postUser() throws Exception {
        String username = RandomHelper.randomAlphabetic(15);
        String nickname = RandomHelper.randomAlphabetic(10);
        User posted = createUser(username, nickname);
        Validator.Validate("validate user nick name", posted.getNickName(), nickname);

        User stored = Identity.UserGetByUserId(posted.getId());
        Validator.Validate("validate user name", posted.getUsername(), stored.getUsername());
    }

    @Test(groups = "dailies")
    //https://oculus.atlassian.net/browse/SER-436
    //Please insure users cannot have same username as nickname
    public void testNickNameDifferentFromUsername() throws Exception {
        String username = RandomHelper.randomAlphabetic(15);
        String nickname = RandomHelper.randomAlphabetic(15);
        User user = createUser(username, nickname);

        user.setNickName(username);
        String url = Identity.IdentityV1UserURI + "/" + IdConverter.idToHexString(user.getId());
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(
                url, JsonHelper.JsonSerializer(user),
                HttpclientHelper.HttpRequestType.put, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "be the same as username";
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));

        UserPersonalInfo updatedPII = createUserNamePII(user.getNickName(), user.getId());
        user.setUsername(updatedPII.getId());
        response = HttpclientHelper.PureHttpResponse(url, JsonHelper.JsonSerializer(user), HttpclientHelper.HttpRequestType.put, nvps);
        Validator.Validate("validate response error code", 400, response.getStatusLine().getStatusCode());
        Validator.Validate("validate response error message", true,
                EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));

        updatedPII = createUserNamePII(RandomHelper.randomAlphabetic(15), user.getId());
        user.setUsername(updatedPII.getId());
        Identity.UserPut(user);
    }

    @Test(groups = "dailies")
    public void testCheckName() throws Exception {
        User user = Identity.UserPostDefault();
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));

        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-username/" + RandomHelper.randomAlphabetic(15) ,
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator randomUsername valid", 200, response.getStatusLine().getStatusCode());
        response.close();

        UserPersonalInfo loginName = Identity.UserPersonalInfoGetByUserPersonalInfoId(user.getUsername());
        UserLoginName userLoginName = (UserLoginName)JsonHelper.JsonNodeToObject(loginName.getValue(), UserLoginName.class);
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-username/" + userLoginName.getUserName() ,
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator duplicate username", 409, response.getStatusLine().getStatusCode());
        response.close();

        response =  HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-username/" + RandomHelper.randomAlphabetic(2) ,
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator username length", 400, response.getStatusLine().getStatusCode());
        response.close();

        response =  HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-username/" + RandomHelper.randomAlphabetic(50) ,
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator username length", 400, response.getStatusLine().getStatusCode());
        response.close();

        response =  HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-username/" + RandomHelper.randomNumeric(2) + RandomHelper.randomAlphabetic(10) ,
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator username not start with string", 400, response.getStatusLine().getStatusCode());
        response.close();
    }

    @Test(groups = "dailies")
    public void testCheckEmail() throws Exception {
        User user = Identity.UserPostDefault();
        Email email = IdentityModel.DefaultEmail();
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo.setLastValidateTime(new Date());
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.EMAIL.toString());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(email));
        userPersonalInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);
        List<UserPersonalInfoLink> links = new ArrayList<>();
        UserPersonalInfoLink link = new UserPersonalInfoLink();
        link.setIsDefault(true);
        link.setLabel(RandomHelper.randomAlphabetic(15));
        link.setValue(userPersonalInfo.getId());
        links.add(link);
        user.setEmails(links);
        user = Identity.UserPut(user);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));

        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-email/" + IdentityModel.DefaultEmail().getInfo(),
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator randomUsername valid", 200, response.getStatusLine().getStatusCode());
        response.close();

        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-email/" + RandomHelper.randomNumeric(15),
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator randomUsername valid", 400, response.getStatusLine().getStatusCode());
        response.close();

        Email newEmail = IdentityModel.DefaultEmail();
        userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo.setLastValidateTime(new Date());
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.EMAIL.toString());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(newEmail));
        userPersonalInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);
        links.get(0).setIsDefault(false);
        UserPersonalInfoLink newLink = new UserPersonalInfoLink();
        newLink.setIsDefault(true);
        newLink.setLabel(RandomHelper.randomAlphabetic(15));
        newLink.setValue(userPersonalInfo.getId());
        links.add(newLink);
        user.setEmails(links);
        user = Identity.UserPut(user);

        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-email/" + IdentityModel.DefaultEmail().getInfo(),
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator randomUsername valid", 200, response.getStatusLine().getStatusCode());
        response.close();

        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-email/" + RandomHelper.randomNumeric(15),
                "", HttpclientHelper.HttpRequestType.post, nvps);
        Validator.Validate("Validator randomUsername valid", 400, response.getStatusLine().getStatusCode());
        response.close();
    }

    protected static User createUser(String username, String nickName) throws Exception {
        User user = IdentityModel.DefaultUser();
        user.setNickName(nickName);
        user = Identity.UserPostDefault(user);
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.USERNAME.toString());
        UserLoginName loginName = new UserLoginName();
        loginName.setUserName(username);
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(loginName));
        UserPersonalInfo loginInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);
        user.setIsAnonymous(false);
        user.setUsername(loginInfo.getId());
        return Identity.UserPut(user);
    }

    protected static UserPersonalInfo createUserNamePII(String username, UserId userId) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        UserLoginName userLoginName = new UserLoginName();
        userLoginName.setUserName(username);
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(userLoginName));
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.USERNAME.toString());
        return Identity.UserPersonalInfoPost(userId, userPersonalInfo);
    }
}
