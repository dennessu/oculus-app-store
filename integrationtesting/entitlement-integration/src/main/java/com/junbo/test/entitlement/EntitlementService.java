/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement;

import com.junbo.common.id.EntitlementId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.test.identity.Identity;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by jiefeng on 14-3-25.
 */
public class EntitlementService {

    private static String entitlementUrl = RestUrl.getRestUrl(RestUrl.ComponentName.COMMERCE);
    private static LogHelper logger = new LogHelper(Entitlement.class);
    private static AsyncHttpClient asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());

    public EntitlementService() {
    }

    public static String grantEntitlement(Entitlement entitlement) throws Exception {
        return grantEntitlement(entitlement, 200);
    }

    public static String grantEntitlement(Entitlement entitlement, int expectedResponseCode) throws Exception {
        String requestBody = new JsonMessageTranscoder().encode(entitlement);

        String entitlementEndpointUrl = entitlementUrl + "entitlements/";

        Request req = new RequestBuilder("POST")
                .setUrl(entitlementEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .setBody(requestBody)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());
        Entitlement rtnEntitlement = new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {
        },
                nettyResponse.getResponseBody());
        String rtnEntitlementId = IdConverter.idToHexString(new EntitlementId(rtnEntitlement.getEntitlementId()));
        Master.getInstance().addEntitlement(rtnEntitlementId, rtnEntitlement);
        return rtnEntitlementId;
    }


    public String getEntitlement(String entitlementId) throws Exception {
        return null;
    }

    public List<String> getEntitlements(String userId, String developerId) throws Exception {
        return getEntitlements(userId, developerId, 200);
    }

    public List<String> getEntitlements(String userId, String developerId, int expectedResponseCode) throws Exception {
        String entitlementEndpointUrl = entitlementUrl
                + "users/"
                + userId
                + "/entitlements?developerId="
                + developerId;

        Request req = new RequestBuilder("GET")
                .setUrl(entitlementEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());
        Results<Entitlement> rtnEntitlements =
                new JsonMessageTranscoder().decode(new TypeReference<Results<Entitlement>>() {
                },
                        nettyResponse.getResponseBody());
        List<String> rtnEntitlementIds = new ArrayList<>();

        for (Entitlement en : rtnEntitlements.getItems()) {
            String rtnEntitlementId = IdConverter.idToHexString(new EntitlementId(en.getEntitlementId()));
            Master.getInstance().addEntitlement(rtnEntitlementId, en);
            rtnEntitlementIds.add(rtnEntitlementId);
        }
        return rtnEntitlementIds;
    }

    public static User createDeveloper() throws Exception {
        User developerUser = Identity.DefaultPostUser(); // create an developer
        Entitlement developerEntitlement = new Entitlement();
        developerEntitlement.setUserId(developerUser.getId().getValue());
        developerEntitlement.setEntitlementDefinitionId(0L);// create DEVELOPER definitionId
        grantEntitlement(developerEntitlement);
        return developerUser;
    }
}
