/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model;

import com.junbo.test.billing.enums.TransactionType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiyu_000 on 6/26/14.
 */
public class BillingHistory {

    public BillingHistory() {
        paymentInfos = new ArrayList<>();
        refundOrderItemInfos = new ArrayList<>();
    }

    private BigDecimal totalAmount;
    private List<PaymentInstrumentInfo> paymentInfos;
    private TransactionType transactionType;
    private List<RefundOrderItemInfo> refundOrderItemInfos;

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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public List<RefundOrderItemInfo> getRefundOrderItemInfos() {
        return refundOrderItemInfos;
    }

    public void setRefundOrderItemInfos(List<RefundOrderItemInfo> refundOrderItemInfos) {
        this.refundOrderItemInfos = refundOrderItemInfos;
    }
}
