/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xmchen on 14-4-17.
 */
@Entity
@Table(name = "balance_item_event")
public class BalanceItemEventEntity extends BaseEntity {
    @Id
    @Column(name = "event_id")
    private Long id;

    @Column(name = "balance_item_id")
    private Long balanceItemId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "action_type_id")
    private Short actionTypeId;

    @Column(name = "event_date")
    private Date eventDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalanceItemId() {
        return balanceItemId;
    }

    public void setBalanceItemId(Long balanceItemId) {
        this.balanceItemId = balanceItemId;
    }

    public Short getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(Short actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
