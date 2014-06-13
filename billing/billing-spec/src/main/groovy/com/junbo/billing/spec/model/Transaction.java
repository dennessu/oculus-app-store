/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.TransactionId;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xmchen on 14-2-24.
 */
public class Transaction extends ResourceMetaForDualWrite<TransactionId> {
    @JsonIgnore
    private TransactionId id;
    @JsonIgnore
    private BalanceId balanceId;

    private PaymentInstrumentId piId;
    private String type;
    private String paymentRefId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private Date transactionTime;

    @Override
    public TransactionId getId() {
        return this.id;
    }

    @Override
    public void setId(TransactionId id) {
        this.id = id;
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

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }
}
