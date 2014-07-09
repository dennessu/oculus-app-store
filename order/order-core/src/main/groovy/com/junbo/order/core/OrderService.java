/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.core.impl.order.OrderServiceContext;
import com.junbo.order.spec.model.*;

import java.util.List;

/**
 * Created by chriszhu on 2/7/14.
 */
public interface OrderService {

    Promise<Order> settleQuote(Order order, OrderServiceContext orderServiceContext);

    Promise<Order> createQuote(Order order, OrderServiceContext orderServiceContext);

    Promise<Order> getOrderByOrderId(Long orderId, Boolean doRate, OrderServiceContext context);

    Promise<Order> cancelOrder(Order request, OrderServiceContext orderServiceContext);

    Promise<Order> refundOrCancelOrder(Order request, OrderServiceContext orderServiceContext);

    Promise<List<Order>> getOrdersByUserId(Long userId, OrderServiceContext context, OrderQueryParam orderQueryParam, PageParam pageParam);

    Promise<OrderEvent> updateOrderByOrderEvent(OrderEvent event, OrderServiceContext orderServiceContext);

    Promise<Order> updateTentativeOrder(Order order, OrderServiceContext orderServiceContext);

    Promise<Order> updateNonTentativeOrder(Order order, OrderServiceContext orderServiceContext);
}
