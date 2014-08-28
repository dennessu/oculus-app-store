/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.test.common.ConfigHelper;

import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.order.apihelper.OrderEventService;

/**
 * @author Jason
 *         Time: 5/7/2014
 *         The implementation for order event related APIs
 */
public class OrderEventServiceImpl extends HttpClientBase implements OrderEventService {

    private static String orderEventUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/order-events";
    private OAuthService oAuthTokenClient = OAuthServiceImpl.getInstance();
    private LogHelper logger = new LogHelper(OrderEventServiceImpl.class);
    private static OrderEventService instance;
    private boolean isServiceScope = true;

    public static synchronized OrderEventService getInstance() {
        if (instance == null) {
            instance = new OrderEventServiceImpl();
        }
        return instance;
    }

    private OrderEventServiceImpl() {
        componentType = ComponentType.ORDER;
    }

    public OrderEvent postOrderEvent(OrderEvent orderEvent) throws Exception {
        return postOrderEvent(orderEvent, 200);
    }

    public OrderEvent postOrderEvent(OrderEvent orderEvent, int expectedResponseCode) throws Exception {
        String responseBody;
        if (orderEvent.getAction().toLowerCase().contains("charge")) {
            responseBody = restApiCall(HTTPMethod.POST, orderEventUrl, orderEvent, expectedResponseCode);
        } else {
            oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.ORDER);
            responseBody = restApiCall(HTTPMethod.POST, orderEventUrl, orderEvent, expectedResponseCode, true);
        }
        return new JsonMessageTranscoder().decode(new TypeReference<OrderEvent>() {
        }, responseBody);
    }

    @Override
    public Results<OrderEvent> getOrderEventsByOrderId(String orderId) throws Exception {
        return getOrderEventsByOrderId(orderId, 200);
    }

    @Override
    public Results<OrderEvent> getOrderEventsByOrderId(String orderId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, orderEventUrl + "?orderId=" + orderId, expectedResponseCode,
                isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<OrderEvent>>() {
        }, responseBody);
    }

    @Override
    public OrderEvent getOrderEvent(String orderEventId) throws Exception {
        return getOrderEvent(orderEventId, 200);
    }

    @Override
    public OrderEvent getOrderEvent(String orderEventId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, orderEventUrl + "/" + orderEventId, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<OrderEvent>() {
        }, responseBody);
    }

}
