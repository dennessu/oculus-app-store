/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.SubledgerId;

/**
 * Created by chriszhu on 2/10/14.
 */
public class SubledgerItem extends BaseModelWithDate {
    private SubledgerId subledgerId;
    private OrderItemId orderItemId;
    private Integer resourceAge;

    public SubledgerId getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(SubledgerId subledgerId) {
        this.subledgerId = subledgerId;
    }

    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getRev() {
        return resourceAge;
    }

    public void setRev(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }
}
