/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.*;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.math.BigDecimal;

/**
 * Created by chriszhu on 2/10/14.
 */
public class SubledgerItem extends ResourceMetaForDualWrite<SubledgerItemId> {
    @JsonProperty("self")
    private SubledgerItemId id;
    private SubledgerId subledger;
    private SubledgerItemId originalSubledgerItem;
    private BigDecimal totalAmount;
    private BigDecimal totalPayoutAmount;
    private Long totalQuantity;
    private OrderItemId orderItem;
    @XSSFreeString
    private String subledgerItemAction;
    private OfferId offer;
    @XSSFreeString
    private String status;

    public SubledgerItemId getId() {
        return id;
    }

    public void setId(SubledgerItemId id) {
        this.id = id;
    }

    public SubledgerId getSubledger() {
        return subledger;
    }

    public void setSubledger(SubledgerId subledger) {
        this.subledger = subledger;
    }

    public SubledgerItemId getOriginalSubledgerItem() {
        return originalSubledgerItem;
    }

    public void setOriginalSubledgerItem(SubledgerItemId originalSubledgerItem) {
        this.originalSubledgerItem = originalSubledgerItem;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalPayoutAmount() {
        return totalPayoutAmount;
    }

    public void setTotalPayoutAmount(BigDecimal totalPayoutAmount) {
        this.totalPayoutAmount = totalPayoutAmount;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public OrderItemId getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItemId orderItem) {
        this.orderItem = orderItem;
    }

    public String getSubledgerItemAction() {
        return subledgerItemAction;
    }

    public void setSubledgerItemAction(String subledgerItemAction) {
        this.subledgerItemAction = subledgerItemAction;
    }

    public OfferId getOffer() {
        return offer;
    }

    public void setOffer(OfferId offer) {
        this.offer = offer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
