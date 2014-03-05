/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.TransactionId;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-2-24.
 */
public class Transaction {
    @JsonProperty("self")
    private TransactionId transactionId;
    private BalanceId balanceId;
    private PaymentInstrumentId piId;
    private String type;
    private String paymentRefId;
    private String status;
    private BigDecimal amount;
    private String currency;

    public TransactionId getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(TransactionId transactionId) {
        this.transactionId = transactionId;
    }

    public BalanceId getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(BalanceId balanceId) {
        this.balanceId = balanceId;
    }

    public PaymentInstrumentId getPiId() {
        return piId;
    }

    public void setPiId(PaymentInstrumentId piId) {
        this.piId = piId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentRefId() {
        return paymentRefId;
    }

    public void setPaymentRefId(String paymentRefId) {
        this.paymentRefId = paymentRefId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
