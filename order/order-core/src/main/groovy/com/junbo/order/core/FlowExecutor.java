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

/**
 * Created by fzhang on 14-3-5.
 */
public interface FlowExecutor {
    Promise<List<Order>> executeFlow(FlowType flowType, OrderServiceContext order);
}
