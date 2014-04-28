/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;

import java.math.BigDecimal;

/**
 * Tax item for order item & shipping fee.
 */
public class OrderTaxItem {
    @JsonIgnore
    private Long taxItemId;
    private String taxType;
    @JsonIgnore
    private OrderId orderId;
    @JsonIgnore
    private OrderItemId orderItemId;
    private BigDecimal taxAmount;
    private BigDecimal taxRate;
    private Boolean isTaxExempted;
    @JsonIgnore
    private String type;

    public Long getTaxItemId() {
        return taxItemId;
    }

    public void setTaxItemId(Long taxItemId) {
        this.taxItemId = taxItemId;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public Boolean getIsTaxExempted() {
        return isTaxExempted;
    }

    public void setIsTaxExempted(Boolean isTaxExempted) {
        this.isTaxExempted = isTaxExempted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
