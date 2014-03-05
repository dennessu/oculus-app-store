/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core;

import com.junbo.order.core.impl.order.OrderServiceContext;

/**
 * Created by fzhang on 14-2-26.
 */
public interface FlowSelector {
    OrderFlow select(OrderServiceContext expOrder, OrderServiceOperation operation);
}
