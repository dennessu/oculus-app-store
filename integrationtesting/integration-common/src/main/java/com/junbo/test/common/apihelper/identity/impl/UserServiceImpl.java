/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.*;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.identity.spec.model.user.User;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Jason
 * time 3/10/2014
 * User related API helper, including get/post/put user, update password and so on.
 */
public class UserServiceImpl implements UserService {

    private final String identityServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.IDENTITY) + "users";
    private final String oAuthServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.OAUTH) + "/auth";

    private LogHelper logger = new LogHelper(UserServiceImpl.class);
    private AsyncHttpClient asyncClient;

    private static UserService instance;

    public static synchronized UserService instance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    private UserServiceImpl() {
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public String PostUser() throws Exception {

        User user = new User();
        user.setUserName(RandomFactory.getRandomEmailAddress());
        user.setPassword("password");
        user.setStatus(EnumHelper.UserStatus.ACTIVE.toString());

        return PostUser(user);
    }

    public String PostUser(User user) throws Exception {
        return PostUser(user, 200);
    }

    public String PostUser(User user, int expectedResponseCode) throws Exception {

        Request req = new RequestBuilder("POST")
                .setUrl(identityServerURL)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .setBody(new JsonMessageTranscoder().encode(user))
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {},
                nettyResponse.getResponseBody());
        Master.getInstance().addUser(IdConverter.idToHexString(userGet.getId()), userGet);
        return IdConverter.idToHexString(userGet.getId());
    }

    public String GetUserByUserId(String userId) throws Exception {
        return GetUserByUserId(userId, 200);
    }

    public String GetUserByUserId(String userId, int expectedResponseCode) throws Exception {

        String url = identityServerURL + "/" + userId;

        Request req = new RequestBuilder("GET")
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .setUrl(url)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {},
                nettyResponse.getResponseBody());
        Master.getInstance().addUser(IdConverter.idToHexString(userGet.getId()), userGet);
        return IdConverter.idToHexString(userGet.getId());
    }

    public List<String> GetUserByUserName(String userName) throws Exception {
        return GetUserByUserName(userName, 200);
    }

    public List<String> GetUserByUserName(String userName, int expectedResponseCode) throws Exception {

        Request req = new RequestBuilder("GET")
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .addQueryParameter("userName", userName)
                .setUrl(identityServerURL)
                .build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);

        Results<User> userGet = new JsonMessageTranscoder().decode(
               new TypeReference<Results<User>>() {}, nettyResponse.getResponseBody());

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
