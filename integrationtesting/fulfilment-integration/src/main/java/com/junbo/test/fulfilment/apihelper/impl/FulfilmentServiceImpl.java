/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.fulfilment.apihelper.impl;

import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.fulfilment.apihelper.FulfilmentService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

/**
 * Created by yunlongzhao on 5/14/14.
 */
public class FulfilmentServiceImpl extends HttpClientBase implements FulfilmentService {

    private static String fulfilmentUrl = ConfigHelper.getSetting("defaultCommerceEndpoint");
    private static FulfilmentService instance;
    private OAuthService oAuthTokenClient = OAuthServiceImpl.getInstance();

    public static synchronized FulfilmentService getInstance() {
        if (instance == null) {
            instance = new FulfilmentServiceImpl();
        }
        return instance;
    }

    private FulfilmentServiceImpl() {
        componentType = ComponentType.FULFILMENT;
    }

    @Override
    public String postFulfilment(FulfilmentRequest fulfilment) throws Exception {
        return postFulfilment(fulfilment, 200);
    }

    @Override
    public String postFulfilment(FulfilmentRequest fulfilment, int expectedResponseCode) throws Exception {
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType);
        String responseBody = restApiCall(HTTPMethod.POST, fulfilmentUrl + "/fulfilments", fulfilment,
                expectedResponseCode, true);

        FulfilmentRequest fulfilmentResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<FulfilmentRequest>() {
                        }, responseBody);

        String fulfilmentId = fulfilmentResult.getRequestId().toString();
        Master.getInstance().addFulfilment(fulfilmentId, fulfilmentResult);

        return fulfilmentId;
    }

    @Override
    public String getFulfilmentByOrderId(String orderId) throws Exception {
        return getFulfilmentByOrderId(orderId, 200);
    }

    @Override
    public String getFulfilmentByOrderId(String orderId, int expectedResponseCode) throws Exception {
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType);
        String responseBody = restApiCall(HTTPMethod.GET, String.format(fulfilmentUrl + "/fulfilments?orderId=%s",
                orderId), expectedResponseCode, true);

        FulfilmentRequest fulfilmentResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<FulfilmentRequest>() {
                        }, responseBody);

        String fulfilmentId = fulfilmentResult.getRequestId().toString();
        Master.getInstance().addFulfilment(fulfilmentId, fulfilmentResult);
        return fulfilmentId;
    }

    @Override
    public FulfilmentItem getFulfilment(String fulfilmentId) throws Exception {
        return getFulfilment(fulfilmentId, 200);
    }

    @Override
    public FulfilmentItem getFulfilment(String fulfilmentId, int expectedResponseCode) throws Exception {
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType);
        String responseBody = restApiCall(HTTPMethod.GET, fulfilmentUrl + "/fulfilments/" + fulfilmentId,
                expectedResponseCode, true);

        FulfilmentItem fulfilmentItemResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<FulfilmentItem>() {
                        }, responseBody);


        return fulfilmentItemResult;
    }

}
