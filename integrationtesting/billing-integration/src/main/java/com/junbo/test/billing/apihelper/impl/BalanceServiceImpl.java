/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.apihelper.impl;

import com.junbo.billing.spec.model.Balance;
import com.junbo.common.model.Results;
import com.junbo.test.billing.apihelper.BalanceService;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 4/8/14.
 */
public class BalanceServiceImpl extends HttpClientBase implements BalanceService {

    private static String balanceUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1");
    private static BalanceService instance;
    private String userId;
    private OAuthService oAuthTokenClient = OAuthServiceImpl.getInstance();


    public void setUserId(String userId) {
        this.userId = userId;
    }

    private static String requestorId = "CheckoutService";
    private static String onBehalfOfRequestorId = "DigitalGameStore";
    private static String userIp = "157.123.45.67";
    private static String fakeBalanceId = "\"self\" : {\n" +
            "    \"href\" : \"http://api.oculusvr-demo.com:8081/v1/balances/0355ED747CDF\",\n" +
            "    \"id\" : \"0355ED747CDF\"\n" +
            "  }";


    public static synchronized BalanceService getInstance() {
        if (instance == null) {
            instance = new BalanceServiceImpl();
        }
        return instance;
    }

    private BalanceServiceImpl() {
        componentType = ComponentType.BILLING;
    }

    /*
    @Override
    protected FluentCaseInsensitiveStringsMap getHeader() {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        headers.add(Header.CONTENT_TYPE, contentType);
        headers.add(Header.DELEGATE_USER_ID, String.format("users/%s", userId));
        headers.add(Header.REQUESTOR_ID, requestorId);
        headers.add(Header.ON_BEHALF_OF_REQUESTOR_ID, onBehalfOfRequestorId);
        headers.add(Header.USER_IP, userIp);

        return headers;
    }
    */

    @Override
    public String postBalance(String uid, Balance balance) throws Exception {
        return postBalance(uid, balance, 200);
    }

    @Override
    public String postBalance(String uid, Balance balance, int expectedResponseCode) throws Exception {
        setUserId(uid);
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType.BILLING);
        String responseBody = restApiCall(HTTPMethod.POST, balanceUrl + "balances", balance, true);

        Balance balanceResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<Balance>() {
                        }, responseBody);

        String balanceId = IdConverter.idToHexString(balanceResult.getId());
        Master.getInstance().addBalances(balanceId, balanceResult);

        return balanceId;
    }

    @Override
    public String getBalanceByBalanceId(String uid, String balanceId) throws Exception {
        return getBalanceByBalanceId(uid, balanceId, 200);
    }

    @Override
    public String getBalanceByBalanceId(String uid, String balanceId, int expectedResponseCode) throws Exception {
        setUserId(uid);
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType.BILLING);
        String responseBody = restApiCall(HTTPMethod.GET, balanceUrl +
                "balances/" + balanceId, expectedResponseCode, true);

        Balance balanceResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<Balance>() {
                        }, responseBody);

        balanceId = IdConverter.idToHexString(balanceResult.getId());
        Master.getInstance().addBalances(balanceId, balanceResult);

        return balanceId;
    }

    @Override
    public String quoteBalance(String uid, Balance balance) throws Exception {
        return this.quoteBalance(uid, balance, 200);
    }

    @Override
    public String quoteBalance(String uid, Balance balance, int expectedResponseCode) throws Exception {
        this.setUserId(uid);
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType.BILLING);
        String responseBody = restApiCall(HTTPMethod.POST, balanceUrl + "balances/quote", balance);

        responseBody = responseBody.replace("\"self\" : null", fakeBalanceId);

        Balance balanceResult =
                new JsonMessageTranscoder().decode(
                        new TypeReference<Balance>() {
                        }, responseBody);

        String balanceId = IdConverter.idToHexString(balanceResult.getId());
        Master.getInstance().addBalances(balanceId, balanceResult);

        return balanceId;
    }

    @Override
    public List<String> getBalancesByOrderId(String orderId) throws Exception {
        return getBalancesByOrderId(orderId, 200);
    }

    @Override
    public List<String> getBalancesByOrderId(String orderId, int expectedResponseCode) throws Exception {
        //setUserId(uid);
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType.BILLING);
        String responseBody = restApiCall(HTTPMethod.GET, balanceUrl +
                "balances?orderId=" + orderId, expectedResponseCode);

        Results<Balance> balanceResults =
                new JsonMessageTranscoder().decode(
                        new TypeReference<Results<Balance>>() {
                        }, responseBody);

        List<String> balanceList = new ArrayList<>();
        for (Balance balanceResult : balanceResults.getItems()) {
            String balanceId = IdConverter.idToHexString(balanceResult.getId());
            balanceList.add(balanceId);
            Master.getInstance().addBalances(balanceId, balanceResult);
        }

        return balanceList;
    }
}
