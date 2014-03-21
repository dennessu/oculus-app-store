/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.order.impl;

import com.junbo.testing.common.apihelper.HttpClientBase;
import com.junbo.testing.common.apihelper.order.OrderService;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;

import javax.persistence.criteria.Order;
import java.util.List;

/**
 * Created by Yunlong on 3/21/14.
 */
public class OrderServiceImpl extends HttpClientBase implements OrderService {

    private static String orderUrl = RestUrl.getRestUrl(RestUrl.ComponentName.ORDER);

    private LogHelper logger = new LogHelper(OrderServiceImpl.class);

    private static OrderService instance;

    @Override
    public String postOrder(Order order) throws Exception {
        return null;
    }

    @Override
    public String postOrder(Order order, int expectedResponseCode) throws Exception {
        return null;
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
        return null;
    }

    @Override
    public String getOrderByOrderId(String orderId, int expectedResponseCode) throws Exception {
        return null;
    }

    @Override
    public String updateOrderByOrderId(String orderId) throws Exception {
        return null;
    }

    @Override
    public String updateOrderByOrderId(String orderId, int expectedResponseCode) throws Exception {
        return null;
    }
}
