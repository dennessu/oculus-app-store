/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.transaction;

import com.junbo.billing.db.BaseEventEntity;
import com.junbo.billing.db.EntityValidationCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by xmchen on 14-1-24.
 */
@Entity
@Table(name = "transaction_event")
public class TransactionEventEntity extends BaseEventEntity {
    private Long transactionId;

    @Column(name = "transaction_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
