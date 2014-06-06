/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.order.utility;

import com.junbo.order.spec.model.Order;
import com.junbo.test.common.Entities.enums.OrderStatus;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class OrderValidationHelper extends BaseValidationHelper {

    public void validateOrderStatus(Map<String, OrderStatus> expectedOrderStatus) {
        Set<String> key = expectedOrderStatus.keySet();
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            String orderId = (String) it.next();
            Order order = Master.getInstance().getOrder(orderId);
            verifyEqual(order.getStatus(), expectedOrderStatus.get(orderId).toString(), "verify orderStatus");
        }
    }

}
