/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "billing_transaction")
public class TransactionEntity extends BaseEntity {
    @Id
    @Column(name = "transaction_id")
    private Long id;

    @Column(name = "pi_id")
    private Long piId;

    @Column(name = "balance_id")
    private Long balanceId;

    @Column(name = "type_id")
    private Short typeId;

    @Column(name = "payment_ref_id")
    private String paymentRefId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "status_id")
    private Short statusId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    public Long getPiId() {
        return piId;
    }
    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public Short getTypeId() {
        return typeId;
    }
    public void setTypeId(Short typeId) {
        this.typeId = typeId;
    }

    public String getPaymentRefId() {
        return paymentRefId;
    }
    public void setPaymentRefId(String paymentRefId) {
        this.paymentRefId = paymentRefId;
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

    public Short getStatusId() {
        return statusId;
    }
    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }
}
