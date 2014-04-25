/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.model.ResourceMeta;

/**
 * Created by LinYi on 2/10/14.
 */
public class PreorderUpdateHistory extends ResourceMeta {
    private String updatedType;
    private String updatedColumn;
    private String beforeValue;
    private String afterValue;
    private Long id;
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

    @JsonIgnore
    public Long getOrderItemPreorderUpdateHistoryId() {
        return id;
    }

    public void setOrderItemPreorderUpdateHistoryId(Long orderItemPreorderUpdateHistoryId) {

        this.id = orderItemPreorderUpdateHistoryId;
    }

    @JsonIgnore
    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }
}
