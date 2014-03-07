/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo;

import com.junbo.common.id.BalanceId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.order.db.entity.enums.BillingAction;
import com.junbo.order.db.entity.enums.EventStatus;
import com.junbo.order.spec.model.*;

import java.util.List;

/**
 * Created by chriszhu on 2/18/14.
 */
public interface OrderRepository {

    Order createOrder(Order order, OrderEvent orderEvent);

    Order getOrder(Long orderId);

    OrderEvent createOrderEvent(OrderEvent event);

    FulfillmentEvent createFulfillmentEvent(FulfillmentEvent event);

    void saveBillingEvent(OrderId orderId, BalanceId balanceId,
                          BillingAction action, EventStatus status);

    List<OrderItem> getOrderItems(Long orderId);

    List<Discount> getDiscounts(Long orderId);

    List<PaymentInstrumentId> getPaymentInstrumentIds(Long orderId);
}
