/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.jobs.transaction.model;

import com.junbo.common.id.OrderId;
import com.junbo.order.spec.model.fb.TransactionType;

import java.math.BigDecimal;

/**
 * Created by acer on 2015/1/29.
 */
public class FacebookTransaction {

    private OrderId orderId;

    private TransactionType txnType;

    private String providerTxnId;

    private String fbPaymentId;

    private BigDecimal senderAmount;

    private BigDecimal usdAmount;

    private String currency;

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public TransactionType getTxnType() {
        return txnType;
    }

    public void setTxnType(TransactionType txnType) {
        this.txnType = txnType;
    }

    public String getProviderTxnId() {
        return providerTxnId;
    }

    public void setProviderTxnId(String providerTxnId) {
        this.providerTxnId = providerTxnId;
    }

    public String getFbPaymentId() {
        return fbPaymentId;
    }

    public void setFbPaymentId(String fbPaymentId) {
        this.fbPaymentId = fbPaymentId;
    }

    public BigDecimal getSenderAmount() {
        return senderAmount;
    }

    public void setSenderAmount(BigDecimal senderAmount) {
        this.senderAmount = senderAmount;
    }

    public BigDecimal getUsdAmount() {
        return usdAmount;
    }

    public void setUsdAmount(BigDecimal usdAmount) {
        this.usdAmount = usdAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
