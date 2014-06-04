/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

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
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 *         time 3/10/2014
 *         User related API helper, including get/post/put user, update password and so on.
 */
public class UserServiceImpl extends HttpClientBase implements UserService {

    private final String identityServerURL = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "/users";
    private static UserService instance;

    public static synchronized UserService instance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    private UserServiceImpl() {
    }

    public String PostUser() throws Exception {
        User userForPost = new User();
        userForPost.setIsAnonymous(false);
        userForPost.setStatus("ACTIVE");
        userForPost.setUsername(RandomFactory.getRandomStringOfAlphabet(10));

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, userForPost, 201);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {
        },
                responseBody);

        String userId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userId, userGet);

        UserId userIdDefault = userGet.getId();

        //attach user email and address info
        UserPersonalInfo email = postEmail(userIdDefault);
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

    private UserPersonalInfo postAddress(UserId userId) throws Exception {

        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("ADDRESS");
        userPersonalInfo.setUserId(userId);
        String str = "{\"street1\":\"19800 MacArthur Blvd\",\"city\":\"Irvine\",\"postalCode\":\"92612\"," +
                "\"country\":{\"id\":\"US\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postEmail(UserId userId) throws Exception {

        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("EMAIL");
        userPersonalInfo.setUserId(userId);
        GregorianCalendar gc = new GregorianCalendar();
        userPersonalInfo.setLastValidateTime(gc.getTime());
        String str = "{\"info\":\"" + RandomFactory.getRandomEmailAddress() + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }

    private UserPersonalInfo postEmail(UserId userId, String emailAddress) throws Exception {

        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType("EMAIL");
        userPersonalInfo.setUserId(userId);
        GregorianCalendar gc = new GregorianCalendar();
        userPersonalInfo.setLastValidateTime(gc.getTime());
        String str = "{\"info\":\"" + RandomFactory.getRandomEmailAddress() + "\"}";
        if (emailAddress != null && !emailAddress.isEmpty()) {
            str = "{\"info\":\"" + emailAddress + "\"}";
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        userPersonalInfo.setValue(value);

        return postUserPersonalInfo(userPersonalInfo);
    }


    private UserPersonalInfo postUserPersonalInfo(UserPersonalInfo userPersonalInfo) throws Exception {
        return postUserPersonalInfo(userPersonalInfo, 201);
    }

    private UserPersonalInfo postUserPersonalInfo(UserPersonalInfo userPersonalInfo, int expectedResponseCode) throws Exception {
        String serverURL = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "/personal-info";
        String responseBody = restApiCall(HTTPMethod.POST, serverURL, userPersonalInfo, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<UserPersonalInfo>() {
        }, responseBody);
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

        return userRtnId;
    }

    public List<String> GetUserByUserName(String userName) throws Exception {
        return GetUserByUserName(userName, 200);
    }

    public List<String> GetUserByUserName(String userName, int expectedResponseCode) throws Exception {

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

}
