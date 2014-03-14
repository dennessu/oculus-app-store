/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.identity.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.EnumHelper;
import com.junbo.testing.common.libs.RandomFactory;
import com.junbo.testing.common.blueprint.User;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;

import java.util.concurrent.Future;

/**
 * @author Jason
 * time 3/10/2014
 * User related API helper, including get/post/put user, update password and so on.
 */
public class UserServiceImpl implements UserService {

    private final String requestHeaderName = "Content-Type";
    private final String requestHeaderValue = "application/json";

    private LogHelper logger = new LogHelper(UserServiceImpl.class);
    private String serverUrl;
    private AsyncHttpClient asyncClient;

    public UserServiceImpl(String url) {
        serverUrl = url;
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public String PutUser(String userName, String status) throws Exception {
        //Todo
        return "";
    };

    //Authenticate user
    public String AuthenticateUser(String userName, String password) throws Exception {
        //Todo
        return "";
    };

    //update password
    public String UpdatePassword(String userId, String oldPassword, String newPassword) throws Exception {
        //Todo
        return "";
    };

    //reset password
    public String ResetPassword(String userId, String newPassword) throws Exception {
        //Todo
        return "";
    };

    public String GetUserByUserId(String userId) throws Exception {

        String url = serverUrl + "/" + userId;

        Request req = new RequestBuilder("GET")
                .addHeader(requestHeaderName, requestHeaderValue)
                .setUrl(url)
                .build();
        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        return nettyResponse.getResponseBody();
    }

    public String GetUserByUserName(String userName) throws Exception {

        Request req = new RequestBuilder("GET")
                .addHeader(requestHeaderName, requestHeaderValue)
                .addParameter("userName", userName)
                .setUrl(serverUrl)
                .build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);
        return nettyResponse.getResponseBody();
    }

    public String PostUser() throws Exception {

        String userName = RandomFactory.getRandomEmailAddress();
        String password = "password";
        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return PostUser(userName, password, status);
    }

    public String PostUser(String userName) throws Exception {

        String password = "password";
        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return PostUser(userName, password, status);
    }

    public String PostUser(String userName, String password) throws Exception {

        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return PostUser(userName, password, status);
    }

    public String PostUser(String userName, String password, String status) throws Exception {

        String requestBody = "";

        User userToPost = new User();
        userToPost.setUserName(userName);
        userToPost.setStatus(status);
        userToPost.setPassword(password);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        requestBody = ow.writeValueAsString(userToPost);

        Request req = new RequestBuilder("POST")
                .setUrl(serverUrl)
                .addHeader(requestHeaderName, requestHeaderValue)
                .setBody(requestBody)
                .build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);
        return nettyResponse.getResponseBody();
    }
}
