/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.model.ResourceMetaForDualWrite;

/**
 * Created by LinYi on 2/10/14.
 */
public class PreorderUpdateHistory extends ResourceMetaForDualWrite<Long> {

    @JsonIgnore
    private Long id;

    private String updatedType;
    private String updatedColumn;
    private String beforeValue;
    private String afterValue;

    @JsonIgnore
    private OrderItemId orderItemId;

    public String getUpdatedType() {
        return updatedType;
    }

    public void setUpdatedType(String updatedType) {
        this.updatedType = updatedType;
    }

    public String getUpdatedColumn() {
        return updatedColumn;
    }

    public void setUpdatedColumn(String updatedColumn) {
        this.updatedColumn = updatedColumn;
    }

    public String getBeforeValue() {
        return beforeValue;
    }

    public void setBeforeValue(String beforeValue) {
        this.beforeValue = beforeValue;
    }

    public String getAfterValue() {
        return afterValue;
    }

    public void setAfterValue(String afterValue) {
        this.afterValue = afterValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }
}
