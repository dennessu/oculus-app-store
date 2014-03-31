/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.EntityValidationCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "order_balance_link")
public class OrderBalanceLinkEntity extends BaseEntity {
    private Long linkId;
    private Long orderId;
    private Long balanceId;

    @Id
    @Column(name = "order_balance_link_id")
    public Long getLinkId() {
        return linkId;
    }
    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    @Column(name = "order_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "balance_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

}
