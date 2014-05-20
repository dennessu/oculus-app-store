/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.apihelper;

import com.junbo.order.spec.model.OrderEvent;

/**
 @author Jason
  * Time: 5/7/2014
  * The interface for order event related APIs
 */
public interface OrderEventService {
    OrderEvent postOrderEvent(OrderEvent orderEvent) throws Exception;
    OrderEvent postOrderEvent(OrderEvent orderEvent, int expectedResponseCode) throws Exception;
}
