/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.testing.common.apihelper.order;

import javax.persistence.criteria.Order;
import java.lang.Exception;
import java.util.List;

/**
 * Created by Yunlong on 3/21/14.
 */
public interface OrderService {
    //return orderId in blueprint
    String postOrder(Order order) throws Exception;

    String postOrder(Order order, int expectedResponseCode) throws Exception;

    List<String> getOrderByUserId(String userId) throws Exception;

    List<String> getOrderByUserId(String userId, int expectedResponseCode) throws Exception;

    String getOrderByOrderId(String orderId) throws Exception;

    String getOrderByOrderId(String orderId, int expectedResponseCode) throws Exception;

    String updateOrderByOrderId(String orderId) throws Exception;

    String updateOrderByOrderId(String orderId, int expectedResponseCode) throws Exception;

}
