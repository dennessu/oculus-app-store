/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.EntityValidationCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "billing_transaction")
public class TransactionEntity extends BaseEntity {
    private Long transactionId;
    private Long piId;
    private Long balanceId;
    private Short typeId;
    private String paymentRefId;
    private BigDecimal amount;
    private String currency;
    private Short statusId;

    @Id
    @Column(name = "transaction_id")
    public Long getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    @Column(name = "balance_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    @Column(name = "pi_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getPiId() {
        return piId;
    }
    public void setPiId(Long piId) {
        this.piId = piId;
    }

    @Column(name = "type_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Short getTypeId() {
        return typeId;
    }
    public void setTypeId(Short typeId) {
        this.typeId = typeId;
    }

    @Column(name = "payment_ref_id")
    public String getPaymentRefId() {
        return paymentRefId;
    }
    public void setPaymentRefId(String paymentRefId) {
        this.paymentRefId = paymentRefId;
    }

    @Column(name = "amount")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "currency")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max = 3, message = EntityValidationCode.TOO_LONG)
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "status_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Short getStatusId() {
        return statusId;
    }
    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }
}
