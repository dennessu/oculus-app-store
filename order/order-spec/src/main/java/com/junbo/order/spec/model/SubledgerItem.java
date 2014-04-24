/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.SubledgerId;
import com.junbo.common.id.SubledgerItemId;
import com.junbo.common.model.BaseResource;

import java.math.BigDecimal;

/**
 * Created by chriszhu on 2/10/14.
 */
public class SubledgerItem extends BaseResource {
    @JsonProperty("self")
    private SubledgerItemId subledgerItemId;
    private SubledgerId subledgerId;
    private SubledgerItemId originalSubledgerItemId;
    private BigDecimal totalAmount;
    private OrderItemId orderItemId;
    private OfferId offerId;
    private String subledgerItemAction;
    private String status;
    private Integer resourceAge;

    public SubledgerItemId getSubledgerItemId() {
        return subledgerItemId;
    }

    public void setSubledgerItemId(SubledgerItemId subledgerItemId) {
        this.subledgerItemId = subledgerItemId;
    }

    public SubledgerId getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(SubledgerId subledgerId) {
        this.subledgerId = subledgerId;
    }

    public SubledgerItemId getOriginalSubledgerItemId() {
        return originalSubledgerItemId;
    }

    public void setOriginalSubledgerItemId(SubledgerItemId originalSubledgerItemId) {
        this.originalSubledgerItemId = originalSubledgerItemId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }

    public OfferId getOfferId() {
        return offerId;
    }

    public void setOfferId(OfferId offerId) {
        this.offerId = offerId;
    }

    public String getSubledgerItemAction() {
        return subledgerItemAction;
    }

    public void setSubledgerItemAction(String subledgerItemAction) {
        this.subledgerItemAction = subledgerItemAction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
