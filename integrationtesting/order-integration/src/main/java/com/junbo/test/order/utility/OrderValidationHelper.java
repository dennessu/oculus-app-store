/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.order.utility;

import com.junbo.common.model.Results;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;
import com.junbo.test.order.model.enums.OrderStatus;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class OrderValidationHelper extends BaseValidationHelper {
    OrderTestDataProvider testDataProvider = new OrderTestDataProvider();

    public void validateOrderStatus(Map<String, OrderStatus> expectedOrderStatus) {
        Set<String> key = expectedOrderStatus.keySet();
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            String orderId = (String) it.next();
            Order order = Master.getInstance().getOrder(orderId);
            verifyEqual(order.getStatus(), expectedOrderStatus.get(orderId).toString(), "verify orderStatus");
        }
    }

    public void validateOrderEvents(String orderId, Map<OrderActionType, EventStatus> expectedOrderEvents)
            throws Exception {
        Results<OrderEvent> orderEventResults = testDataProvider.getOrderEventsByOrderId(orderId);
        List<OrderEvent> orderEvents = orderEventResults.getItems();

        verifyEqual(orderEvents.size(), expectedOrderEvents.size(), "verify order events size");

        Set<OrderActionType> key = expectedOrderEvents.keySet();
        int i = 0;
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            OrderActionType orderActionType = (OrderActionType) it.next();

            EventStatus eventStatus = expectedOrderEvents.get(orderActionType);
            verifyEqual(orderEvents.get(i).getAction(), orderActionType.toString(), "verify order action type");
            verifyEqual(orderEvents.get(i).getStatus(), eventStatus.toString(), "verify event status");
            i++;
        }
    }

}
