/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.FulfilmentId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.OrderItemId;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

/**
 * FulfilmentItem.
 */
public class FulfilmentItem {
    @Null
    @FulfilmentId
    private Long fulfilmentId;

    @OrderItemId
    private Long orderItemId;

    @OfferId
    private Long offerId;

    private Long timestamp;
    private Integer quantity;

    @JsonIgnore
    private Long requestId;

    @Null
    @JsonProperty("fulfilmentActions")
    private List<FulfilmentAction> actions = new ArrayList();

    public void addFulfilmentAction(FulfilmentAction action) {
        actions.add(action);
    }

    @JsonProperty
    public String getStatus() {
        // aggregated status for billing
        // if all fulfilment action are SUCCEED, the item status is SUCCEED,
        // otherwise PENDING
        if (actions == null) {
            return FulfilmentStatus.SUCCEED;
        }

        for (FulfilmentAction action : actions) {
            if (!Utils.equals(FulfilmentStatus.SUCCEED, action.getStatus())) {
                return FulfilmentStatus.PENDING;
            }
        }

        return FulfilmentStatus.SUCCEED;
    }

    public Long getFulfilmentId() {
        return fulfilmentId;
    }

    public void setFulfilmentId(Long fulfilmentId) {
        this.fulfilmentId = fulfilmentId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<FulfilmentAction> getActions() {
        return actions;
    }

    public void setActions(List<FulfilmentAction> actions) {
        this.actions = actions;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
