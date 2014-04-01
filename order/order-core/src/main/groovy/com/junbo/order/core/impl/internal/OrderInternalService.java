/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core.impl.internal;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Order;

import java.util.List;

/**
 * Created by chriszhu on 4/1/14.
 */
public interface OrderInternalService {
    Promise<Order> rateOrder(Order order);

    Promise<Order> getOrderByOrderId(Long orderId);

    Promise<List<Order>> getOrdersByUserId(Long userId);

}
