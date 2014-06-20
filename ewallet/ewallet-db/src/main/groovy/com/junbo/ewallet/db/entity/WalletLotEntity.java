/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity of WalletLot.
 */
@javax.persistence.Entity
@Table(name = "ewallet_lot")
public class WalletLotEntity extends BaseEntity {
    @Column(name = "ewallet_id")
    private Long walletId;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "total")
    private BigDecimal totalAmount;

    @Column(name = "remaining")
    private BigDecimal remainingAmount;

    @Column(name = "expiration_date")
    private Date expirationDate;

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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return walletId;
    }
}
