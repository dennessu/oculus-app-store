/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity of Transaction.
 */
@javax.persistence.Entity
@Table(name = "transaction")
public class TransactionEntity extends BaseEntity {

    @Column(name = "tracking_uuid")
    @Type(type = "pg-uuid")
    private UUID trackingUuid;

    @Column(name = "ewallet_id")
    private Long walletId;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "offer_id")
    private Long offerId;

    @Column(name = "unrefunded_amount")
    private BigDecimal unrefundedAmount;

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public BigDecimal getUnrefundedAmount() {
        return unrefundedAmount;
    }

    public void setUnrefundedAmount(BigDecimal unrefundedAmount) {
        this.unrefundedAmount = unrefundedAmount;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return walletId;
    }
}
