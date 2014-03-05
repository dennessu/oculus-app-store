/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.core.impl.order.OrderServiceContext;
import com.junbo.order.spec.model.Order;

import java.util.List;
import java.util.UUID;

/**
 * Created by chriszhu on 2/7/14.
 */
public interface OrderFlow {

    UUID getName();
    Promise<List<Order>> execute(OrderServiceContext order);
}
