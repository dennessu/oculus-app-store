/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import com.junbo.common.model.Results;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;

import java.util.List;

class OrderResultList extends Results<Order> {
    @Override
    public List<Order> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<Order> items) {
        super.setItems(items);
    }
}
class OrderEventResultList extends Results<OrderEvent> {
    @Override
    public List<OrderEvent> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<OrderEvent> items) {
        super.setItems(items);
    }
}
