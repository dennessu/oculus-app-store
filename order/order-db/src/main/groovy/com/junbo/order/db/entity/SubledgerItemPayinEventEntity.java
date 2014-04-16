/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Created by chriszhu on 1/29/14.
 */

@Entity
@Table(name = "SUBLEDGER_ITEM_EVENT")
public class SubledgerItemPayinEventEntity extends CommonEventEntity {
    private Long subledgerItemId;

    @Column(name = "SUBLEDGER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSubledgerItemId() {
        return subledgerItemId;
    }

    public void setSubledgerItemId(Long subledgerItemId) {
        this.subledgerItemId = subledgerItemId;
    }

    @Override
    @Transient
    public Long getShardId() {
        return subledgerItemId;
    }
}
