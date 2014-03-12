/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.EnumHelper;
import com.junbo.testing.common.libs.RandomFactory;
import com.junbo.testing.common.blueprint.User;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Jason
 * time 3/10/2014
 * Post User API helper
 */
public class PostUser {

    private static final String requestHeaderName = "Content-Type";
    private static final String requestHeaderValue = "application/json";

    private static LogHelper logger = new LogHelper(PostUser.class);

    public static String CreateUser(String url) {

        String userName = RandomFactory.getRandomEmailAddress();
        String password = "password";
        String passwordStrength = EnumHelper.PasswordStrength.WEAK.toString();
        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return CreateUser(url, userName, password, passwordStrength, status);
    }

    public static String CreateUser(String url, String userName) {

        String password = "password";
        String passwordStrength = EnumHelper.PasswordStrength.WEAK.toString();
        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return CreateUser(url, userName, password, passwordStrength, status);
    }

    public static String CreateUser(String url, String userName, String password, String passwordStrength) {

        String status = EnumHelper.UserStatus.ACTIVE.toString();

        return CreateUser(url, userName, password, passwordStrength, status);
    }

    public static String CreateUser(String url, String userName, String password, String passwordStrength,
                                    String status) {

        String requestBody = "";

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        AsyncHttpClient client = new AsyncHttpClient(builder.build());

        User userToPost = new User();

        userToPost.setUserName(userName);
        userToPost.setStatus(status);
        userToPost.setPassword(password);
        userToPost.setPasswordStrength(passwordStrength);

        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            requestBody = ow.writeValueAsString(userToPost);
        }
        catch (JsonProcessingException pe) {
            pe.printStackTrace();
        }

        logger.logInfo("The POST request URL is: " + url);
        logger.logInfo("The POST request body is: " + requestBody);

        try {
            Request req = new RequestBuilder("POST")
                    .setUrl(url)
                    .addHeader(requestHeaderName, requestHeaderValue)
                    .setBody(requestBody)
                    .build();
            Future future = client.prepareRequest(req).execute();
            NettyResponse nettyResponse = (NettyResponse) future.get();
            return nettyResponse.getResponseBody();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}
