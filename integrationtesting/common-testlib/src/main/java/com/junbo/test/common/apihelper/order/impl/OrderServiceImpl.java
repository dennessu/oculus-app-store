/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.order.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.order.spec.model.Order;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.order.OrderService;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;

import java.util.List;

/**
 * Created by Yunlong on 3/21/14.
 */
public class OrderServiceImpl extends HttpClientBase implements OrderService {

    private static String orderUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/orders/";

    private LogHelper logger = new LogHelper(OrderServiceImpl.class);

    private static OrderService instance;

    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderServiceImpl();
        }
        return instance;
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
    public String postOrder(Order order) throws Exception {
        return postOrder(order, 200);
    }

    @Override
    public String postOrder(Order order, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, orderUrl, order, expectedResponseCode);

        Order orderResult = new JsonMessageTranscoder().decode(
                new TypeReference<Order>() {
                }, responseBody);

        String orderId = IdConverter.idToHexString(orderResult.getId());
        Master.getInstance().addOrder(orderId, orderResult);

        return orderId;
    }

    @Override
    public List<String> getOrderByUserId(String userId) throws Exception {
        return null;
    }

    @Override
    public List<String> getOrderByUserId(String userId, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String getOrderByOrderId(String orderId) throws Exception {
        return getOrderByOrderId(orderId, 200);
    }

    @Override
    public String getOrderByOrderId(String orderId, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.GET, orderUrl + orderId, expectedResponseCode);

        Order orderResult = new JsonMessageTranscoder().decode(
                new TypeReference<Order>() {
                }, responseBody);

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

        String responseBody = restApiCall(HTTPMethod.PUT, orderUrl + orderId, order, expectedResponseCode);

        if (expectedResponseCode == 200) {
            Order orderResult = new JsonMessageTranscoder().decode(
                    new TypeReference<Order>() {
                    }, responseBody);

            String responseOrderId = IdConverter.idToHexString(orderResult.getId());
            Master.getInstance().addOrder(orderId, orderResult);

            return responseOrderId;
        }
        return null;
    }

}
