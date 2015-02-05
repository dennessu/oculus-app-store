/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.BillingAction;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 1/26/14.
 */
@Entity
@Table(name= "ORDER_BILLING_HISTORY")
public class OrderBillingHistoryEntity extends CommonDbEntityWithDate {
    private Long historyId;
    private Long orderId;
    private String balanceId;
    private BillingAction billingEventId;
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private Boolean isTaxInclusive;
    private Boolean success;
    private String note;

    @Id
    @Column(name = "HISTORY_ID")
    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    @Column(name = "ORDER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "BALANCE_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    @Column (name = "BILLING_EVENT_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.BillingActionType")
    public BillingAction getBillingEventId() {
        return billingEventId;
    }

    public void setBillingEventId(BillingAction billingEventId) {
        this.billingEventId = billingEventId;
    }

    @Column(name = "TOTAL_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "TOTAL_TAX")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    @Column(name = "IS_TAX_INCLUSIVE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getIsTaxInclusive() {
        return isTaxInclusive;
    }

    public void setIsTaxInclusive(Boolean isTaxInclusive) {
        this.isTaxInclusive = isTaxInclusive;
    }

    @Override
    @Transient
    public Long getShardId() {
        return getHistoryId();
    }

    @Column(name = "SUCCESS")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Column(name = "NOTE")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
