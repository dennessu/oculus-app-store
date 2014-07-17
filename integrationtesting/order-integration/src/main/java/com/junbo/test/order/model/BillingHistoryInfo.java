/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model;

import com.junbo.test.order.model.enums.BillingAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiyu_000 on 6/26/14.
 */
public class BillingHistoryInfo {

    public BillingHistoryInfo() {
        paymentInfos = new ArrayList<>();
        refundOrderItemInfos = new ArrayList<>();
    }

    private BigDecimal totalAmount;
    private List<PaymentInstrumentInfo> paymentInfos;

    private BillingAction billingAction;
    private List<RefundOrderItemInfo> refundOrderItemInfos;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    private boolean success;

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<PaymentInstrumentInfo> getPaymentInfos() {
        return paymentInfos;
    }

    public void setPaymentInfos(List<PaymentInstrumentInfo> paymentInfos) {
        this.paymentInfos = paymentInfos;
    }

    public BillingAction getBillingAction() {
        return billingAction;
    }

    public void setBillingAction(BillingAction billingAction) {
        this.billingAction = billingAction;
    }

    public List<RefundOrderItemInfo> getRefundOrderItemInfos() {
        return refundOrderItemInfos;
    }

    public void setRefundOrderItemInfos(List<RefundOrderItemInfo> refundOrderItemInfos) {
        this.refundOrderItemInfos = refundOrderItemInfos;
    }
}
