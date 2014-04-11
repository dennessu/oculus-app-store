/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.*;

import java.util.List;
import java.util.UUID;

/**
 * Created by chriszhu on 2/7/14.
 */
public interface OrderService {

    Promise<Order> settleQuote(Order order, ApiContext context);

    Promise<Order> createQuote(Order order, ApiContext context);

    Promise<Order> getOrderByOrderId(Long orderId);

    Promise<Order> cancelOrder(Order request);

    Promise<Order> refundOrder(Order request);

    Promise<List<Order>> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam);

    Promise<OrderEvent> updateOrderBillingStatus(OrderEvent event);

    Promise<OrderEvent> updateOrderFulfillmentStatus(OrderEvent event);

    Promise<Order> updateTentativeOrder(Order order, ApiContext context);

    Order getOrderByTrackingUuid(UUID trackingUuid, Long userId);

    Promise<Order> completeChargeOrder(Long orderId, ApiContext context);
}
