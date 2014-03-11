/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.user;

import com.junbo.testing.common.libs.LogHelper;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 @author Jason
  * Time: 3/10/2014
  * Get User API helper
 */
public class GetUser {

    private static final String requestHeaderName = "Content-Type";
    private static final String requestHeaderValue = "application/json";

    private static LogHelper logger = new LogHelper(GetUser.class);

    public static String GetUserById(String url, String userId){

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        AsyncHttpClient client = new AsyncHttpClient(builder.build());

        String getUserUrl = url + "/" + userId;

        try {
            Request req = new RequestBuilder("GET")
                    .addHeader(requestHeaderName, requestHeaderValue)
                    .setUrl(getUserUrl)
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

    public static String GetUserByUserName(String url, String userName){

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        AsyncHttpClient client = new AsyncHttpClient(builder.build());

        try {
            Request req = new RequestBuilder("GET")
                    .addHeader(requestHeaderName, requestHeaderValue)
                    .addParameter("userName", userName)
                    .setUrl(url)
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
