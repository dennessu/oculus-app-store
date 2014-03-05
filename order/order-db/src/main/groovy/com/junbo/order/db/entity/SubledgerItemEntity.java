/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 1/29/14.
 */
@Entity
@Table(name = "SUBLEDGER_ITEM")
public class SubledgerItemEntity extends CommonDbEntityWithDate{
    private Long subledgerItemId;
    private Long originalSubledgerItemId;
    private Long subledgerId;
    private BigDecimal totalAmount;
    private Long orderItemId;

    @Id
    @Column(name = "SUBLEDGER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSubledgerItemId() {
        return subledgerItemId;
    }

    public void setSubledgerItemId(Long subledgerItemId) {
        this.subledgerItemId = subledgerItemId;
    }

    @Column(name = "ORIGINAL_SUBLEDGER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOriginalSubledgerItemId() {
        return originalSubledgerItemId;
    }

    public void setOriginalSubledgerItemId(Long originalSubledgerItemId) {
        this.originalSubledgerItemId = originalSubledgerItemId;
    }

    @Column(name = "SUBLEDGER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(Long subledgerId) {
        this.subledgerId = subledgerId;
    }

    @Column(name = "TOTAL_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "ORDER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }
}
