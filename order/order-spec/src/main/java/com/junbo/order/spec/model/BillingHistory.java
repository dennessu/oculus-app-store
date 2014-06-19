/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by fzhang on 14-3-11.
 */
public class BillingHistory extends ResourceMetaForDualWrite<Long> {

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private String balanceId;

    @JsonIgnore
    private Long orderId;

    private BigDecimal totalAmount;

    private String billingEvent;

    private List<BillingPaymentInfo> payments;

    private List<RefundOrderItem> refundedOrderItems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    public String getBillingEvent() {
        return billingEvent;
    }

    public void setBillingEvent(String billingEvent) {
        this.billingEvent = billingEvent;
    }

    public List<BillingPaymentInfo> getPayments() {
        return payments;
    }

    public void setPayments(List<BillingPaymentInfo> payments) {
        this.payments = payments;
    }

    public List<RefundOrderItem> getRefundedOrderItems() {
        return refundedOrderItems;
    }

    public void setRefundedOrderItems(List<RefundOrderItem> refundedOrderItems) {
        this.refundedOrderItems = refundedOrderItems;
    }
}
