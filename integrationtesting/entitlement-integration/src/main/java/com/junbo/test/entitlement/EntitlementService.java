/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;

import java.util.concurrent.Future;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by jiefeng on 14-3-25.
 */
public class EntitlementService {

    private static String commerceUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1");
    private static LogHelper logger = new LogHelper(Entitlement.class);
    private static AsyncHttpClient asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());

    private EntitlementService() {
    }

    public static Entitlement grantEntitlement(Entitlement entitlement) throws Exception {
        return grantEntitlement(entitlement, 200);
    }

    public static Entitlement grantEntitlement(Entitlement entitlement, int expectedResponseCode) throws Exception {
        String requestBody = new String(new JsonMessageTranscoder().encode(entitlement));

        String entitlementEndpointUrl = commerceUrl + "/entitlements/";

        Request req = new RequestBuilder("POST")
                .setUrl(entitlementEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .setBody(requestBody)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
        if (expectedResponseCode == 200) {
            return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {
            },
                    nettyResponse.getResponseBody());
        }
        return null;
    }


    public static Entitlement getEntitlement(String entitlementId) throws Exception {
        return getEntitlement(entitlementId, 200);
    }

    public static Entitlement getEntitlement(String entitlementId, int expectedResponseCode) throws Exception {
        String entitlementGetURI = commerceUrl + "/entitlements/" + entitlementId;
        Request req = new RequestBuilder("GET")
                .setUrl(entitlementGetURI)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .build();
        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
        if (expectedResponseCode == 200) {
            return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {
            },
                    nettyResponse.getResponseBody());
        }
        return null;
    }

    public static Entitlement updateEntitlement(String entitlementId, Entitlement entitlement)
            throws Exception {
        return updateEntitlement(entitlementId, entitlement, 200);
    }

    public static Entitlement updateEntitlement(String entitlementId, Entitlement entitlement, int expectedResponseCode)
            throws Exception {

        String requestBody = new String(new JsonMessageTranscoder().encode(entitlement));

        String entitlementEndpointUrl = commerceUrl + "/entitlements/" + entitlementId;

        Request req = new RequestBuilder("PUT")
                .setUrl(entitlementEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .setBody(requestBody)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
        if (expectedResponseCode == 200) {
            return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {
            },
                    nettyResponse.getResponseBody());
        }
        return null;
    }

    public static void deleteEntitlement(String entitlementId) throws Exception {
        deleteEntitlement(entitlementId, 204);
    }

    public static void deleteEntitlement(String entitlementId, int expectedResponseCode)
            throws Exception {
        String entitlementGetURI = commerceUrl + "/entitlements/" + entitlementId;
        Request req = new RequestBuilder("DELETE")
                .setUrl(entitlementGetURI)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .build();
        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
    }

    public static Results<Entitlement> getEntitlements(String userId) throws Exception {
        return getEntitlements(userId, 200);
    }

    public static Results<Entitlement> getEntitlements(String userId, int expectedResponseCode) throws Exception {
        String entitlementEndpointUrl = commerceUrl + "/entitlements" + "?userId=" + userId;
        Request req = new RequestBuilder("GET")
                .setUrl(entitlementEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .build();
        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        assertEquals(nettyResponse.getStatusCode(), expectedResponseCode);
        if (expectedResponseCode == 200) {
            return new JsonMessageTranscoder().decode(new TypeReference<Results<Entitlement>>() {
            },
                    nettyResponse.getResponseBody());
        }
        return null;
    }
}
