/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.id.UserId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;

import java.util.*;

/**
 * @author Jason
 *         time 3/10/2014
 *         User related API helper, including get/post/put user, update password and so on.
 */
public class UserServiceImpl extends HttpClientBase implements UserService {

    private final String identityServerURL = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "users";
    private static UserService instance;
    private String userPassword = "Test1234";

    private OAuthService oAuthTokenClient = OAuthServiceImpl.getInstance();

    public static synchronized UserService instance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    private UserServiceImpl() {
    }

    public String PostUser(String userName, String pwd, String emailAddress) throws Exception {
        Master.getInstance().setCurrentUid(null);
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY);
        User userForPost = new User();
        userForPost.setIsAnonymous(false);
        userForPost.setStatus("ACTIVE");
        userForPost.setUsername(userName);

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, userForPost, 201);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);

        String userId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userId, userGet);
        Master.getInstance().setCurrentUid(userId);

        String password = postPassword(userId, pwd);

        oAuthTokenClient.postUserAccessToken(userId, password);

        UserId userIdDefault = userGet.getId();

        //attach user email and address info
        UserPersonalInfo email = postEmail(userIdDefault, emailAddress);
        UserPersonalInfo address = postAddress(userIdDefault);
        UserPersonalInfo phone = postPhone(userIdDefault);
        UserPersonalInfo name = postName(userIdDefault);

        UserPersonalInfoLink piEmail = new UserPersonalInfoLink();
        piEmail.setIsDefault(Boolean.TRUE);
        piEmail.setUserId(userIdDefault);
        piEmail.setValue(email.getId());

        UserPersonalInfoLink piAddress = new UserPersonalInfoLink();
        piAddress.setIsDefault(Boolean.TRUE);
        piAddress.setUserId(userIdDefault);
        piAddress.setValue(address.getId());

        UserPersonalInfoLink piPhone = new UserPersonalInfoLink();
        piPhone.setIsDefault(true);
        piPhone.setUserId(userIdDefault);
        piPhone.setValue(phone.getId());

        UserPersonalInfoLink piName = new UserPersonalInfoLink();
        piName.setIsDefault(true);
        piName.setUserId(userIdDefault);
        piName.setValue(name.getId());

        List<UserPersonalInfoLink> addresses = new ArrayList<>();
        List<UserPersonalInfoLink> emails = new ArrayList<>();
        List<UserPersonalInfoLink> phones = new ArrayList<>();
        addresses.add(piAddress);
        emails.add(piEmail);
        phones.add(piPhone);

        userGet.setAddresses(addresses);
        userGet.setEmails(emails);
        userGet.setPhones(phones);
        userGet.setName(piName);

        this.PutUser(userId, userGet);

        return userId;
    }

    public String PostUser() throws Exception {
        return PostUser(RandomFactory.getRandomStringOfAlphabet(10), null, null);
    }

    private UserPersonalInfo postAddress(UserId userId) throws Exception {

        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("ADDRESS");
        userPersonalInfo.setUserId(userId);
        String str = "{\"street1\":\"800 West Campbell Road\",\"city\":\"Richardson\",\"postalCode\":\"75080\"," +
                "\"country\":{\"id\":\"US\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postName(UserId userId) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("NAME");
        userPersonalInfo.setUserId(userId);
        String str = "{\"givenName\":\"" + RandomFactory.getRandomStringOfAlphabet(5) +
                "\",\"middleName\":\"" + RandomFactory.getRandomStringOfAlphabet(5) +
                "\",\"familyName\":\"" + RandomFactory.getRandomStringOfAlphabet(5) + "\"," +
                "\"nickName\":\"" + RandomFactory.getRandomStringOfAlphabet(5) + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);
        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postPhone(UserId userId) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("PHONE");
        userPersonalInfo.setUserId(userId);
        String str = "{\"info\":\"" + "86131" + RandomFactory.getRandomStringOfNumeric(8) + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postEmail(UserId userId) throws Exception {
        return postEmail(userId, RandomFactory.getRandomEmailAddress());
    }

    private UserPersonalInfo postEmail(UserId userId, String emailAddress) throws Exception {

        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("EMAIL");
        userPersonalInfo.setUserId(userId);
        GregorianCalendar gc = new GregorianCalendar();
        userPersonalInfo.setLastValidateTime(gc.getTime());
        String str;
        if (emailAddress != null && !emailAddress.isEmpty()) {
            str = "{\"info\":\"" + emailAddress + "\"}";
        } else {
            str = "{\"info\":\"" + RandomFactory.getRandomEmailAddress() + "\"}";
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }


    private UserPersonalInfo postUserPersonalInfo(UserPersonalInfo userPersonalInfo) throws Exception {
        return postUserPersonalInfo(userPersonalInfo, 201);
    }

    private UserPersonalInfo postUserPersonalInfo(UserPersonalInfo userPersonalInfo,
                                                  int expectedResponseCode) throws Exception {
        String serverURL = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "personal-info";
        String responseBody = restApiCall(HTTPMethod.POST, serverURL, userPersonalInfo, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<UserPersonalInfo>() {
        }, responseBody);
    }

    private String postPassword(String uid, String pwd) throws Exception {
        Map params = new HashMap();
        String password;
        if (pwd != null && !pwd.isEmpty()) {
            password = pwd;
        } else {
            //password = RandomFactory.getRandomStringOfAlphabet(5);
            password = userPassword;
        }
        params.put("type", "PASSWORD");
        params.put("value", password);
        String requestBody = JSONObject.toJSONString(params);
        restApiCall(HTTPMethod.POST, identityServerURL + "/" + uid + "/" + "change-credentials", requestBody, 201);

        return password;
    }

    public String PostUser(User user) throws Exception {
        return PostUser(user, 201);
    }

    public String PostUser(User user, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, user, expectedResponseCode);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);
        String userId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userId, userGet);

        return userId;
    }

    public String PostUser(String payload) throws Exception {
        return PostUser(payload, 201);
    }

    public String PostUser(String payload, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, payload, expectedResponseCode);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);
        String userId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userId, userGet);

        return userId;
    }

    @Override
    public String PostUser(String userName, String emailAddress) throws Exception {
        User userForPost = new User();
        userForPost.setIsAnonymous(false);
        userForPost.setStatus("ACTIVE");
        userForPost.setUsername(RandomFactory.getRandomStringOfAlphabet(10));
        if (userName != null && !userName.isEmpty()) {
            userForPost.setUsername(userName);
        }

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, userForPost, 201);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);

        String userId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userId, userGet);

        UserId userIdDefault = userGet.getId();

        //attach user email and address info
        UserPersonalInfo email = postEmail(userIdDefault, emailAddress);
        UserPersonalInfo address = postAddress(userIdDefault);

        UserPersonalInfoLink piEmail = new UserPersonalInfoLink();
        piEmail.setIsDefault(Boolean.TRUE);
        piEmail.setUserId(userIdDefault);
        piEmail.setValue(email.getId());

        UserPersonalInfoLink piAddress = new UserPersonalInfoLink();
        piAddress.setIsDefault(Boolean.TRUE);
        piAddress.setUserId(userIdDefault);
        piAddress.setValue(address.getId());

        List<UserPersonalInfoLink> addresses = new ArrayList<>();
        List<UserPersonalInfoLink> emails = new ArrayList<>();
        addresses.add(piAddress);
        emails.add(piEmail);

        userGet.setAddresses(addresses);
        userGet.setEmails(emails);

        this.PutUser(userId, userGet);

        return userId;
    }

    public String GetUserByUserId(String userId) throws Exception {
        return GetUserByUserId(userId, 200);
    }

    public String GetUserByUserId(String userId, int expectedResponseCode) throws Exception {

        String url = identityServerURL;
        if (userId != null && !userId.isEmpty()) {
            url = identityServerURL + "/" + userId;
        }

        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);
        String userRtnId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userRtnId, userGet);
        if (Master.getInstance().getUserAccessToken(userId) == null) {
            oAuthTokenClient.postUserAccessToken(userRtnId, userPassword);
        }
        return userRtnId;
    }

    public List<String> GetUserByUserName(String userName) throws Exception {
        return GetUserByUserName(userName, 200);
    }

    public List<String> GetUserByUserName(String userName, int expectedResponseCode) throws Exception {
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY);
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listUsername = new ArrayList<>();
        listUsername.add(userName);

        paraMap.put("username", listUsername);
        String responseBody = restApiCall(HTTPMethod.GET, identityServerURL, null, expectedResponseCode, paraMap);

        Results<User> userGet = new JsonMessageTranscoder().decode(
                new TypeReference<Results<User>>() {
                }, responseBody);

        List<String> listUserId = new ArrayList<>();
        for (User user : userGet.getItems()) {
            Master.getInstance().addUser(IdConverter.idToHexString(user.getId()), user);
            if (Master.getInstance().getUserAccessToken(IdConverter.idToHexString(user.getId())) == null) {
                if(Master.getInstance().getUserPassword() !=null && !Master.getInstance().getUserPassword().isEmpty())
                oAuthTokenClient.postUserAccessToken(IdConverter.idToHexString(user.getId()),
                        Master.getInstance().getUserPassword());
                else {
                    oAuthTokenClient.postUserAccessToken(IdConverter.idToHexString(user.getId()), userPassword);
                }
            }
            listUserId.add(IdConverter.idToHexString(user.getId()));
        }

        return listUserId;
    }

    public String PutUser(String userId, User user) throws Exception {
        return PutUser(userId, user, 200);
    }

    public String PutUser(String userId, User user, int expectedResponseCode) throws Exception {

        String putUrl = identityServerURL + "/" + userId;
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, user, expectedResponseCode);
        User userPut = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);
        String userRtnId = IdConverter.idToHexString(userPut.getId());
        Master.getInstance().addUser(userRtnId, userPut);

        return userRtnId;
    }

    @Override
    public String PostEmailVerification(String userId, String country, String locale) throws Exception {
        return PostEmailVerification(userId, country, locale, 204);
    }

    @Override
    public String PostEmailVerification(String userId, String country, String locale,
                                        int expectedResponseCode) throws Exception {
        return oAuthTokenClient.postEmailVerification(userId, country, locale, expectedResponseCode);
    }

}
