/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.apihelper.impl;

import com.junbo.common.id.OrderId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.order.spec.model.Subledger;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.Header;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.order.apihelper.OrderService;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 5/19/14.
 */
public class OrderServiceImpl extends HttpClientBase implements OrderService {

    private LogHelper logger = new LogHelper(OrderServiceImpl.class);

    private OAuthService oAuthTokenClient = OAuthServiceImpl.getInstance();

    private static OrderService instance;

    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderServiceImpl();
        }
        return instance;
    }

    private OrderServiceImpl() {
        componentType = ComponentType.ORDER;
    }

    @Override
    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope, List<String> headersToRemove) {
        FluentCaseInsensitiveStringsMap headers = super.getHeader(isServiceScope, headersToRemove);
        headers.add(Header.USER_IP, RandomFactory.getRandomIp());
        return headers;
    }

    @Override
    public String settleQuote(Order order) throws Exception {
        return null;
    }

    @Override
    public String settleQuote(Order order, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String postQuote(Order order) throws Exception {
        return null;
    }

    @Override
    public String postQuote(Order order, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String updateOrderBillingStatus(OrderEvent orderEvent) throws Exception {
        return null;
    }

    @Override
    public String updateOrderBillingStatus(OrderEvent orderEvent, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String updateOrderFulfillmentStatus(OrderEvent orderEvent) throws Exception {
        return null;
    }

    @Override
    public String updateOrderFulfillmentStatus(OrderEvent orderEvent, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String updateTentativeOrder(Order order) throws Exception {
        return null;
    }

    @Override
    public String updateTentativeOrder(Order order, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public Results<Subledger> getSubledgers(String sellerId) throws Exception {
        return getSubledgers(sellerId, 200);
    }

    @Override
    public Results<Subledger> getSubledgers(String sellerId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/subledgers?sellerId=" + sellerId, expectedResponseCode);

        Results<Subledger> subledgerResults = new JsonMessageTranscoder().decode(
                new TypeReference<Results<Subledger>>() {
                }, responseBody
        );

        return subledgerResults;
    }

    @Override
    public String postOrder(Order order) throws Exception {
        return postOrder(order, 200);
    }

    @Override
    public String postOrder(Order order, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/orders/", order, expectedResponseCode);

        if (expectedResponseCode == 200) {
            Order orderResult = new JsonMessageTranscoder().decode(
                    new TypeReference<Order>() {
                    }, responseBody
            );

            String orderId = IdConverter.idToHexString(orderResult.getId());
            Master.getInstance().addOrder(orderId, orderResult);

            return orderId;
        }
        return null;
    }

    @Override
    public List<String> getOrdersByUserId(String userId) throws Exception {
        return getOrdersByUserId(userId, 200);
    }

    @Override
    public List<String> getOrdersByUserId(String userId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/orders?userId=" + userId, expectedResponseCode);

        Results<Order> orderResults = new JsonMessageTranscoder().decode(
                new TypeReference<Results<Order>>() {
                }, responseBody
        );

        List<Order> orderList = orderResults.getItems();
        List<String> orderIdList = new ArrayList<>();
        for (Order order : orderList) {
            String orderId = IdConverter.idToUrlString(OrderId.class, order.getId().getValue());
            Master.getInstance().addOrder(orderId, order);
            orderIdList.add(orderId);
        }
        return orderIdList;
    }

    @Override
    public String getOrderByOrderId(String orderId) throws Exception {
        return getOrderByOrderId(orderId, 200);
    }

    @Override
    public String getOrderByOrderId(String orderId, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/orders/" + orderId, expectedResponseCode);

        Order orderResult = new JsonMessageTranscoder().decode(
                new TypeReference<Order>() {
                }, responseBody
        );

        String responseOrderId = IdConverter.idToHexString(orderResult.getId());
        Master.getInstance().addOrder(orderId, orderResult);

        return responseOrderId;
    }

    @Override
    public String updateOrder(Order order) throws Exception {
        return updateOrder(order, 200);
    }

    @Override
    public String updateOrder(Order order, int expectedResponseCode) throws Exception {
        String orderId = IdConverter.idToHexString(order.getId());

        String responseBody = restApiCall(HTTPMethod.PUT, getEndPointUrl() + "/orders/" + orderId, order, expectedResponseCode);

        if (expectedResponseCode == 200) {
            Order orderResult = new JsonMessageTranscoder().decode(
                    new TypeReference<Order>() {
                    }, responseBody
            );

            String responseOrderId = IdConverter.idToHexString(orderResult.getId());
            Master.getInstance().addOrder(orderId, orderResult);

            return responseOrderId;
        }
        return null;
    }

    @Override
    public String refundOrder(Order order) throws Exception {
        return refundOrder(order, 200);
    }

    @Override
    public String refundOrder(Order order, int expectedResponseCode) throws Exception {
        oAuthTokenClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, componentType);
        String orderId = IdConverter.idToHexString(order.getId());

        String responseBody = restApiCall(HTTPMethod.PUT, getEndPointUrl() + "/orders/" + orderId, order,
                expectedResponseCode, true);

        if (expectedResponseCode == 200) {
            Order orderResult = new JsonMessageTranscoder().decode(
                    new TypeReference<Order>() {
                    }, responseBody
            );

            String responseOrderId = IdConverter.idToHexString(orderResult.getId());
            Master.getInstance().addOrder(orderId, orderResult);

            return responseOrderId;
        }
        return null;
    }

}
