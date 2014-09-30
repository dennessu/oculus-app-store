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

    @Test(groups = "dailies", enabled = false)
    // https://oculus.atlassian.net/browse/SER-553
    // Only CSR can delete user, will disable this temporary
    public void testUpdateUser() throws Exception {
        String username = RandomHelper.randomAlphabetic(15);
        User user = IdentityModel.DefaultUser();
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
        Identity.UserPut(user);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));

        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(
                Identity.IdentityV1UserURI + "/" + IdConverter.idToHexString(user.getId()), null,
                HttpclientHelper.HttpRequestType.delete, nvps);

        Validator.Validate("validate response error code", 403, response.getStatusLine().getStatusCode());
        response.close();
    }

    @Test(groups = "dailies", enabled = false)
    // https://oculus.atlassian.net/browse/SER-553
    // This case will be disabled
    public void testDeleteAndUpdateUser() throws Exception {
        String username = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        User user = Identity.UserPostDefaultWithMail(username, email);

        String password = RandomHelper.randomAlphabetic(15);
        CloseableHttpResponse response = Identity.UserCredentialPostDefault(user.getId(), null, password);
        Validator.Validate("Validate credential create response code", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserPinCredentialPostDefault(user.getId(), password, "1234", false);
        Validator.Validate("Validate credential pin create response code", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(username, password);
        Validator.Validate("Validate credential attempt create response code", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(email, password);
        Validator.Validate("Validate credential attempt with mail create response code", 201, response.getStatusLine().getStatusCode());
        response.close();
        Identity.UserDelete(user);

        User newUser = Identity.UserPostDefaultWithMail(username, email);
        assert user.getId() != newUser.getId();

        String passwordErrorMessage = "User Password Incorrect";
        response = Identity.UserCredentialAttemptesPostDefault(username, password, false);
        Validator.Validate("Validator new user with the same name", 412, response.getStatusLine().getStatusCode());
        Validator.Validate("Validator errorMessage", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(passwordErrorMessage));
        response.close();

        response = Identity.UserPinCredentialAttemptPostDefault(username, "1234", false);
        String pinErrorMessage = "User Pin Incorrect";
        Validator.Validate("Validator new user with the same name pin", 412, response.getStatusLine().getStatusCode());
        Validator.Validate("Validator errorMessage", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(pinErrorMessage));
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(email, password, false);
        Validator.Validate("Validator new user with the same email", 412, response.getStatusLine().getStatusCode());
        Validator.Validate("Validator errorMessage", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(passwordErrorMessage));
        response.close();

        response = Identity.UserPinCredentialAttemptPostDefault(email, "1234", false);
        Validator.Validate("Validator new user with the same email in", 412, response.getStatusLine().getStatusCode());
        Validator.Validate("Validator errorMessage", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(pinErrorMessage));
        response.close();

        Thread.sleep(1000);

        String newPassword = RandomHelper.randomAlphabetic(15);
        response = Identity.UserCredentialPostDefault(newUser.getId(), null, newPassword);
        Validator.Validate("Validate user credential create response code", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserPinCredentialPostDefault(newUser.getId(), newPassword, "5678", false);
        Validator.Validate("Validate credential pin create response code", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(username, password, false);
        Validator.Validate("Validator new user with the same name", 412, response.getStatusLine().getStatusCode());
        Validator.Validate("Validator errorMessage", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(passwordErrorMessage));
        response.close();

        response = Identity.UserPinCredentialAttemptPostDefault(username, "1234", false);
        Validator.Validate("Validator username with old pin", 412, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(username, newPassword);
        Validator.Validate("Validator new user with the new password", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserCredentialAttemptesPostDefault(email, newPassword);
        Validator.Validate("Validator new user email with the new password", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserPinCredentialAttemptPostDefault(username, "5678");
        Validator.Validate("Validator new user with new pin", 201, response.getStatusLine().getStatusCode());
        response.close();

        response = Identity.UserPinCredentialAttemptPostDefault(email, "5678");
        Validator.Validate("Validator new user email with new pin", 201, response.getStatusLine().getStatusCode());
        response.close();

        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.EMAIL.toString());
        Email mailPii = new Email();
        mailPii.setInfo(email);
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(mailPii));
        response = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo, false);
        String message = "Mail is already used.";
        Validator.Validate("Validator userPersonalInfo with same email", 400, response.getStatusLine().getStatusCode());
        Validator.Validate("Validator mail is used", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(message));
        response.close();

        List<User> userList = Identity.UserSearchByUsername(username);
        assert userList != null;
        assert userList.size() == 1;
    }

    @Test(groups = "dailies")
    //https://oculus.atlassian.net/browse/SER-436
    //https://oculus.atlassian.net/browse/SER-639
    //Please insure users can have same username as nickname
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
        Validator.Validate("validate response error code", 200, response.getStatusLine().getStatusCode());
        response.close();

        user = Identity.UserGetByUserId(user.getId());
        UserPersonalInfo updatedPII = createUserNamePII(user.getNickName(), user.getId());
        user.setUsername(updatedPII.getId());
        response = HttpclientHelper.PureHttpResponse(url, JsonHelper.JsonSerializer(user), HttpclientHelper.HttpRequestType.put, nvps);
        Validator.Validate("validate response error code", 200, response.getStatusLine().getStatusCode());
        response.close();

        user = Identity.UserGetByUserId(user.getId());
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
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.EMAIL.toString());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(email));
        userPersonalInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);
        userPersonalInfo.setLastValidateTime(new Date());
        userPersonalInfo = Identity.UserPersonalInfoPut(user.getId(), userPersonalInfo);

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
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.EMAIL.toString());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(newEmail));
        userPersonalInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);
        userPersonalInfo.setLastValidateTime(new Date());
        userPersonalInfo = Identity.UserPersonalInfoPut(user.getId(), userPersonalInfo);
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

    // All Below cases need to
    @Test(groups = "dailies")
    public void testUserPutAddress() throws Exception {
        User user = Identity.UserPostDefaultWithMail(15, "xia.wayne2+" + RandomHelper.randomAlphabetic(15) + "@gmail.com");

        // todo:    Need to check mail is sent
        UserPersonalInfo addressPII = IdentityModel.DefaultUserPersonalInfoAddress();
        addressPII = Identity.UserPersonalInfoPost(user.getId(), addressPII);
        List<UserPersonalInfoLink> links = new ArrayList<>();
        UserPersonalInfoLink link = new UserPersonalInfoLink();
        link.setIsDefault(true);
        link.setUserId(user.getId());
        link.setLabel(RandomHelper.randomAlphabetic(15));
        link.setValue(addressPII.getId());
        links.add(link);
        user.setAddresses(links);
        user = Identity.UserPut(user);

        // todo:    Need to check mail is sent
        UserPersonalInfo addressPII2 = IdentityModel.DefaultUserPersonalInfoAddress();
        addressPII2 = Identity.UserPersonalInfoPost(user.getId(), addressPII2);
        UserPersonalInfoLink link2 = new UserPersonalInfoLink();
        link2.setIsDefault(false);
        link2.setUserId(user.getId());
        link2.setLabel(RandomHelper.randomAlphabetic(15));
        link2.setValue(addressPII2.getId());
        user.getAddresses().add(link2);
        user = Identity.UserPut(user);

        // todo:    Need to check mail is sent
        user.getAddresses().clear();
        user = Identity.UserPut(user);

        UserPersonalInfo unicodeAddressPII2 = IdentityModel.DefaultUserPersonalInfoUnicodeAddress();
        unicodeAddressPII2 = Identity.UserPersonalInfoPost(user.getId(), unicodeAddressPII2);
        Address getAddress = (Address)JsonHelper.JsonNodeToObject(unicodeAddressPII2.getValue(), Address.class);
        assert getAddress.getCity().equalsIgnoreCase("黄埔区");
        UserPersonalInfoLink link3 = new UserPersonalInfoLink();
        link3.setIsDefault(true);
        link3.setUserId(user.getId());
        link3.setLabel(RandomHelper.randomAlphabetic(15));
        link3.setValue(unicodeAddressPII2.getId());
        user.getAddresses().add(link3);
        Identity.UserPut(user);
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-645
    public void testUserPutUsername() throws Exception {
        User user = Identity.UserPostDefaultWithMail(15, "xia.wayne2+" + RandomHelper.randomAlphabetic(15) + "@gmail.com");

        // Change username
        // todo:    Add username check
        UserPersonalInfo usernameInfo = IdentityModel.DefaultUserPersonalInfoUsername();
        usernameInfo = Identity.UserPersonalInfoPost(user.getId(), usernameInfo);
        user.setUsername(usernameInfo.getId());
        Identity.UserPut(user);

        // todo:    Change username to new unicode value
        user = Identity.UserGetByUserId(user.getId());
        UserPersonalInfo nameInfo = IdentityModel.DefaultUnicodeUserPersonalInfoName();
        nameInfo = Identity.UserPersonalInfoPost(user.getId(), nameInfo);
        user.setName(nameInfo.getId());
        Identity.UserPut(user);
    }

    @Test(groups = "dailies")
    public void testEmailChange() throws Exception {
        User user = Identity.UserPostDefaultWithMail(15, RandomHelper.randomAlphabetic(10) + "@gmail.com");

        // change user email
        // todo:    Check mail is sent to mail
        UserPersonalInfo userPersonalInfo = IdentityModel.DefaultUserPersonalInfoEmail("xia.wayne2+" + RandomHelper.randomAlphabetic(15) + "@gmail.com");
        userPersonalInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);
        userPersonalInfo.setLastValidateTime(new Date());
        userPersonalInfo = Identity.UserPersonalInfoPut(user.getId(), userPersonalInfo);
        List<UserPersonalInfoLink> links = new ArrayList<>();
        UserPersonalInfoLink link = new UserPersonalInfoLink();
        link.setValue(userPersonalInfo.getId());
        link.setLabel(RandomHelper.randomAlphabetic(15));
        link.setUserId(user.getId());
        link.setIsDefault(true);
        links.add(link);
        user.setEmails(links);
        user = Identity.UserPut(user);

        // add one mail
        // todo:    Check mail is sent
        userPersonalInfo = IdentityModel.DefaultUserPersonalInfoEmail(RandomHelper.randomAlphabetic(15)+"@gmail.com");
        userPersonalInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);
        UserPersonalInfoLink newLink = new UserPersonalInfoLink();
        newLink.setValue(userPersonalInfo.getId());
        newLink.setLabel(RandomHelper.randomAlphabetic(15));
        newLink.setUserId(user.getId());
        newLink.setIsDefault(false);
        links.add(newLink);
        user.setEmails(links);
        user = Identity.UserPut(user);
    }

    @Test(groups = "dailies")
    public void testCSRUserStatusUpdate() throws Exception {
        User user = Identity.UserPostDefaultWithMail(15, "xia.wayne2+" + RandomHelper.randomAlphabetic(15) + "@gmail.com");

        user.setStatus("SUSPEND");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        nvps.add(new BasicNameValuePair("X-Email-Notification", "true"));

        // todo:    check mail is sent
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/" + IdConverter.idToHexString(user.getId()) ,
                JsonHelper.JsonSerializer(user), HttpclientHelper.HttpRequestType.put, nvps);
        Validator.Validate("Validator randomUsername valid", 200, response.getStatusLine().getStatusCode());
        response.close();

        // todo:    check mail is sent
        user = Identity.UserGetByUserId(user.getId());
        user.setStatus("ACTIVE");
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/" + IdConverter.idToHexString(user.getId()) ,
                JsonHelper.JsonSerializer(user), HttpclientHelper.HttpRequestType.put, nvps);
        Validator.Validate("Validator randomUsername valid", 200, response.getStatusLine().getStatusCode());
        response.close();
    }

    @Test(groups = "dailies")
    public void testCheckUserNameAndEmail() throws Exception {
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-email/" + RandomHelper.randomAlphabetic(15) + "@gmail.com",
                "", HttpclientHelper.HttpRequestType.post, null);
        Validator.Validate("Validate email verification", response.getStatusLine().getStatusCode(), 200);
        response.close();

        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/check-username/" + RandomHelper.randomAlphabetic(15),
                "", HttpclientHelper.HttpRequestType.post, null);
        Validator.Validate("Validate usernmae verification", response.getStatusLine().getStatusCode(), 200);
        response.close();

        User user = Identity.UserPostDefault();

        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/" + IdConverter.idToHexString(user.getId()),
                "", HttpclientHelper.HttpRequestType.get, null);
        Validator.Validate("Validate usernmae verification", response.getStatusLine().getStatusCode(), 403);
        response.close();
    }

    @Test(groups = "dailies")
    // https://oculus.atlassian.net/browse/SER-499
    public void testUserVerifyEmailUpdate() throws Exception {
        User user = Identity.UserPostDefaultWithMail(15, "xia.wayne2+" + RandomHelper.randomAlphabetic(15) + "@gmail.com");

        Email email = IdentityModel.DefaultEmail();
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(email));
        userPersonalInfo.setType(IdentityModel.UserPersonalInfoType.EMAIL.toString());
        userPersonalInfo.setUserId(user.getId());
        userPersonalInfo = Identity.UserPersonalInfoPost(user.getId(), userPersonalInfo);

        UserPersonalInfoLink link = new UserPersonalInfoLink();
        link.setIsDefault(true);
        link.setLabel(RandomHelper.randomAlphabetic(15));
        link.setValue(userPersonalInfo.getId());
        user.getEmails().clear();
        user.getEmails().add(link);

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        CloseableHttpResponse response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/" + IdConverter.idToHexString(user.getId()) ,
                JsonHelper.JsonSerializer(user), HttpclientHelper.HttpRequestType.put, nvps);
        Validator.Validate("Validate Response code", 400, response.getStatusLine().getStatusCode());
        String errorMessage = "Field value is invalid.";
        Validator.Validate("Validate response error message", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();

        userPersonalInfo.setLastValidateTime(new Date());
        user = Identity.UserGetByUserId(user.getId());
        user.getEmails().clear();
        user.getEmails().add(link);
        userPersonalInfo = Identity.UserPersonalInfoPut(user.getId(), userPersonalInfo);
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/" + IdConverter.idToHexString(user.getId()) ,
                JsonHelper.JsonSerializer(user), HttpclientHelper.HttpRequestType.put, nvps);
        Validator.Validate("Validate Response code", 200, response.getStatusLine().getStatusCode());
        response.close();

        user = Identity.UserGetByUserId(user.getId());
        user.getEmails().clear();
        response = HttpclientHelper.PureHttpResponse(Identity.IdentityV1UserURI + "/" + IdConverter.idToHexString(user.getId()) ,
                JsonHelper.JsonSerializer(user), HttpclientHelper.HttpRequestType.put, nvps);
        Validator.Validate("Validate Response code", 400, response.getStatusLine().getStatusCode());
        Validator.Validate("Validate response error message", true, EntityUtils.toString(response.getEntity(), "UTF-8").contains(errorMessage));
        response.close();
    }

    protected static User createUser(String username, String nickName) throws Exception{
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
