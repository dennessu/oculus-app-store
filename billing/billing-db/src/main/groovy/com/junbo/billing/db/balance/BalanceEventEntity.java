/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.balance;

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
@Table(name = "balance_event")
public class BalanceEventEntity extends BaseEventEntity {
    private Long balanceId;

    @Column(name = "balance_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }
}
