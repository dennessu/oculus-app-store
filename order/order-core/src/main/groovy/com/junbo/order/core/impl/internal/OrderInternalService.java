/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core.impl.internal;

import com.junbo.billing.spec.model.Balance;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.core.impl.order.OrderServiceContext;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.order.spec.model.OrderQueryParam;
import com.junbo.order.spec.model.PageParam;
import com.junbo.order.spec.model.enums.BillingAction;

import java.util.List;

/**
 * Created by chriszhu on 4/1/14.
 */
public interface OrderInternalService {
    Promise<Order> rateOrder(Order order, OrderServiceContext context);

    Promise<Order> getOrderByOrderId(Long orderId, OrderServiceContext context);

    Promise<List<Order>> getOrdersByUserId(Long userId, OrderServiceContext context, OrderQueryParam orderQueryParam, PageParam pageParam);

    Order refreshOrderStatus(Order order, boolean updateOrder);

    void markSettlement(Order order);

    Promise<Order> refundOrCancelOrder(Order order, OrderServiceContext orderServiceContext);

    void persistBillingHistory(Balance balance, BillingAction action, Order order);

    OrderEvent checkOrderEventStatus(Order order, OrderEvent event, List<Balance> balances);

    Promise<Order> auditTax(Order order);
}
