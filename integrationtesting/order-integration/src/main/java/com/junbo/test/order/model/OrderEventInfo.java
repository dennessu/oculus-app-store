/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model;

import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;

/**
 * Created by weiyu_000 on 6/16/14.
 */
public class OrderEventInfo {
    public OrderActionType getOrderActionType() {
        return orderActionType;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    OrderActionType orderActionType;
    EventStatus eventStatus;

    public OrderEventInfo(OrderActionType orderActionType, EventStatus eventStatus)
    {
        this.orderActionType = orderActionType;
        this.eventStatus = eventStatus;
    }
}
