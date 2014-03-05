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

    Promise<List<Order>> createOrders(Order order, ApiContext context);

    Promise<List<Order>> settleQuote(Order order, ApiContext context);

    Promise<List<Order>> createQuotes(Order order, ApiContext context);

    Promise<Order> getOrderByOrderId(Long orderId);

    Promise<Order> cancelOrder(Order request);

    Promise<Order> refundOrder(Order request);

    Promise<List<Order>> getOrders(Order request);

    Promise<OrderEvent> updateOrderBillingStatus(OrderEvent event);

    Promise<OrderEvent> updateOrderFulfillmentStatus(OrderEvent event);

    Promise<OrderServiceContext> expandOrder(Order order);

    Promise<OrderServiceContext> expandTentativeOrder(Order order);

    Promise<Order> updateTentativeOrder(Order order, ApiContext context);

}
