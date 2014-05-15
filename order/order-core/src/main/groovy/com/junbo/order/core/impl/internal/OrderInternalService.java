/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core.impl.internal;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderQueryParam;
import com.junbo.order.spec.model.PageParam;

import java.util.List;

/**
 * Created by chriszhu on 4/1/14.
 */
public interface OrderInternalService {
    Promise<Order> rateOrder(Order order);

    Promise<Order> getOrderByOrderId(Long orderId);

    Promise<List<Order>> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam);

    Order refreshOrderStatus(Order order);

    void markSettlement(Order order);

    Promise<Boolean> checkOrderCancelable(Long orderId);

}
