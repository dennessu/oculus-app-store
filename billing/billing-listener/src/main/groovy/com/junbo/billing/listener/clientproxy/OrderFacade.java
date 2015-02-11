/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.listener.clientproxy;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.OrderEvent;

/**
 * Created by xmchen on 14-6-3.
 */
public interface OrderFacade {
    Promise<OrderEvent> postOrderEvent(OrderEvent orderEvent);
}
