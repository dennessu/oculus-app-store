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

    private Long itemReferenceId;

    @OfferId
    private String offerId;
    private Long timestamp;
    private Integer quantity;

    @JsonIgnore
    private Long requestId;

    @Null
    @JsonProperty("fulfilmentActions")
    private List<FulfilmentAction> actions;

    public void addFulfilmentAction(FulfilmentAction action) {
        if (actions == null) {
            actions = new ArrayList<>();
        }

        actions.add(action);
    }

    public Long getFulfilmentId() {
        return fulfilmentId;
    }

    public void setFulfilmentId(Long fulfilmentId) {
        this.fulfilmentId = fulfilmentId;
    }

    public Long getItemReferenceId() {
        return itemReferenceId;
    }

    public void setItemReferenceId(Long itemReferenceId) {
        this.itemReferenceId = itemReferenceId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
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
