/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.common.apihelper.order;

import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;

import java.util.List;

/**
 * Created by Yunlong on 3/21/14.
 */
public interface OrderService {
    //return orderId in blueprint
    String postOrder(Order order) throws Exception;

    String postOrder(Order order, int expectedResponseCode) throws Exception;

    String settleQuote(Order order) throws Exception;

    String settleQuote(Order order, int expectedResponseCode) throws Exception;

    String postQuote(Order order) throws Exception;

    String postQuote(Order order, int expectedResponseCode) throws Exception;

    List<String> getOrderByUserId(String userId) throws Exception;

    List<String> getOrderByUserId(String userId, int expectedResponseCode) throws Exception;

    String getOrderByOrderId(String orderId) throws Exception;

    String getOrderByOrderId(String orderId, int expectedResponseCode) throws Exception;

    String updateOrder(Order order) throws Exception;

    String updateOrder(Order order, int expectedResponseCode) throws Exception;

    String updateOrderBillingStatus(OrderEvent orderEvent) throws Exception;

    String updateOrderBillingStatus(OrderEvent orderEvent, int expectedResponseCode) throws Exception;

    String updateOrderFulfillmentStatus(OrderEvent orderEvent) throws Exception;

    String updateOrderFulfillmentStatus(OrderEvent orderEvent, int expectedResponseCode) throws Exception;

    String updateTentativeOrder(Order order) throws Exception;

    String updateTentativeOrder(Order order, int expectedResponseCode) throws Exception;

}
