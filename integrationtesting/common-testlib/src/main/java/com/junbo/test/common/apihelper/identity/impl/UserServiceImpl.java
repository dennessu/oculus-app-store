/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.*;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.identity.spec.v1.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * time 3/10/2014
 * User related API helper, including get/post/put user, update password and so on.
 */
public class UserServiceImpl extends HttpClientBase implements UserService {

    private final String identityServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.IDENTITY) + "users";
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

        User user = new User();
        user.setUsername(RandomFactory.getRandomStringOfAlphabet(20));
        user.setNickName(RandomFactory.getRandomStringOfAlphabet(10));
        user.setType("user");
        user.setCanonicalUsername(RandomFactory.getRandomStringOfAlphabet(10));
        user.setLocale("en_US");
        user.setPreferredLanguage("en_US");

        return PostUser(user);
    }

    public String PostUser(User user) throws Exception {
        return PostUser(user, 201);
    }

    public String PostUser(User user, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, identityServerURL, user, expectedResponseCode);
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {},
                responseBody);
        String userId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userId, userGet);

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
        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {},
                responseBody);
        String userRtnId = IdConverter.idToHexString(userGet.getId());
        Master.getInstance().addUser(userRtnId, userGet);

        return userRtnId;
    }

    public List<String> GetUserByUserName(String userName) throws Exception {
        return GetUserByUserName(userName, 200);
    }

    public List<String> GetUserByUserName(String userName, int expectedResponseCode) throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        paraMap.put("username", userName);
        String responseBody = restApiCall(HTTPMethod.GET, identityServerURL, null, expectedResponseCode, paraMap);

        Results<User> userGet = new JsonMessageTranscoder().decode(
               new TypeReference<Results<User>>() {}, responseBody);

        List<String> listUserId = new ArrayList<>();
        for (User user : userGet.getItems()){
            Master.getInstance().addUser(IdConverter.idToHexString(user.getId()), user);
            listUserId.add(IdConverter.idToHexString(user.getId()));
        }
        return listUserId;
    }

    public String PutUser(String userName, String status) throws Exception {
        //Todo
        return PutUser(userName, status, 200);
    }

    public String PutUser(String userName, String status, int expectedResponseCode) throws Exception {
        //Todo
        return null;
    }

    //Authenticate user
    public String AuthenticateUser(String userName, String password) throws Exception {
        //Todo
        return AuthenticateUser(userName, password, 200);
    }

    public String AuthenticateUser(String userName, String password, int expectedResponseCode) throws Exception {
        //Todo
        return null;
    }

    //update password
    public String UpdatePassword(String userId, String oldPassword, String newPassword) throws Exception {
        //Todo
        return UpdatePassword(userId, oldPassword, newPassword, 200);
    }

    public String UpdatePassword(String userId, String oldPassword, String newPassword, int expectedResponseCode)
            throws Exception {
        //Todo
        return null;
    }

    //reset password
    public String ResetPassword(String userId, String newPassword) throws Exception {
        //Todo
        return ResetPassword(userId, newPassword, 200);
    }

    public String ResetPassword(String userId, String newPassword, int expectedResponseCode) throws Exception {
        //Todo
        return null;
    }
}
