/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo;

import com.junbo.order.spec.model.*;

import java.util.List;

/**
 * Created by chriszhu on 2/18/14.
 */
public interface OrderRepository {

    Order createOrder(Order order);

    Order getOrder(Long orderId);

    List<Order> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam);

    List<Order> getOrdersByStatus(Object shardKey, List<String> statusList,
                                  boolean updatedByAscending, PageParam pageParam);

    OrderEvent createOrderEvent(OrderEvent event);

    FulfillmentEvent createFulfillmentEvent(Long orderId, FulfillmentEvent event);

    BillingEvent createBillingEvent(Long orderId, BillingEvent event);

    List<OrderItem> getOrderItems(Long orderId);

    OrderItem getOrderItem(Long orderItemId);

    List<Discount> getDiscounts(Long orderId);

    List<PaymentInfo> getPayments(Long orderId);

    Order updateOrder(Order order, boolean updateOnlyOrder);

    List<OrderEvent> getOrderEvents(Long orderId, PageParam pageParam);

    List<PreorderInfo> getPreorderInfo(Long orderItemId);
}
