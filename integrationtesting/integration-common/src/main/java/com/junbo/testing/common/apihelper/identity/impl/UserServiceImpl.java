/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.identity.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.EnumHelper;
import com.junbo.testing.common.libs.RandomFactory;
import com.junbo.testing.common.libs.ConfigPropertiesHelper;
import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.identity.spec.model.user.User;

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

    private final String identityServerURL = "http://" +
            ConfigPropertiesHelper.instance().getProperty("identity.host") +
            ":" +
            ConfigPropertiesHelper.instance().getProperty("identity.port") +
            "/rest/users";
    private final String oAuthServerURL = "http://" +
            ConfigPropertiesHelper.instance().getProperty("oauth.host") +
            ":" +
            ConfigPropertiesHelper.instance().getProperty("oauth.port") +
            "/auth";

    private LogHelper logger = new LogHelper(UserServiceImpl.class);
    private AsyncHttpClient asyncClient;

    public UserServiceImpl() {
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public User PutUser(String userName, String status) throws Exception {
        //Todo
        return null;
    };

    //Authenticate user
    public User AuthenticateUser(String userName, String password) throws Exception {
        //Todo
        return null;
    };

    //update password
    public User UpdatePassword(UserId userId, String oldPassword, String newPassword) throws Exception {
        //Todo
        return null;
    };

    //reset password
    public User ResetPassword(UserId userId, String newPassword) throws Exception {
        //Todo
        return null;
    };

    public User GetUserByUserId(UserId userId) throws Exception {

        String url = identityServerURL + "/" + IdFormatter.encodeId(userId);

        Request req = new RequestBuilder("GET")
                .addHeader(requestHeaderName, requestHeaderValue)
                .setUrl(url)
                .build();
        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);

        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {},
                nettyResponse.getResponseBody());
        return userGet;
    }

    public ResultList<User> GetUserByUserName(String userName) throws Exception {

        Request req = new RequestBuilder("GET")
                .addHeader(requestHeaderName, requestHeaderValue)
                .addQueryParameter("userName", userName)
                .setUrl(identityServerURL)
                .build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);

        ResultList<User> userGet = new JsonMessageTranscoder().decode(
               new TypeReference<ResultList<User>>() {}, nettyResponse.getResponseBody());

        return userGet;
    }

    public User PostUser() throws Exception {

        String userName = RandomFactory.getRandomEmailAddress();
        String password = "password";
        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return PostUser(userName, password, status);
    }

    public User PostUser(String userName) throws Exception {

        String password = "password";
        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return PostUser(userName, password, status);
    }

    public User PostUser(String userName, String password) throws Exception {

        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return PostUser(userName, password, status);
    }

    public User PostUser(String userName, String password, String status) throws Exception {

        String requestBody = "";

        User userToPost = new User();
        userToPost.setUserName(userName);
        userToPost.setStatus(status);
        userToPost.setPassword(password);

        requestBody = new JsonMessageTranscoder().encode(userToPost);

        Request req = new RequestBuilder("POST")
                .setUrl(identityServerURL)
                .addHeader(requestHeaderName, requestHeaderValue)
                .setBody(requestBody)
                .build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);

        User userGet = new JsonMessageTranscoder().decode(new TypeReference<User>() {},
                nettyResponse.getResponseBody());
        return userGet;
    }
}
