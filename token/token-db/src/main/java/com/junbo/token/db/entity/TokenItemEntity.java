/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.entity;

import com.junbo.token.spec.enums.ItemStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Token Item Entity.
 */
@Entity
@Table(name = "token_item")
public class TokenItemEntity extends GenericEntity {

    @Id
    @Column(name = "hash_value")
    private Long hashValue;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "token_item_status")
    private ItemStatus status;

    @Column(name = "disable_reason")
    private String disableReason;

    @Override
    public Long getId() {
        return hashValue;
    }

    @Override
    public void setId(Long id) {
        this.hashValue = id;
    }

    public Long getHashValue() {
        return hashValue;
    }

    public void setHashValue(Long hashValue) {
        this.hashValue = hashValue;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getDisableReason() {
        return disableReason;
    }

    public void setDisableReason(String disableReason) {
        this.disableReason = disableReason;
    }

}
