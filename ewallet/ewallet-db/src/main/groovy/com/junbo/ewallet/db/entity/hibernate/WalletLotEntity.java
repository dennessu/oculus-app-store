/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity.hibernate;

import com.junbo.ewallet.common.def.WalletConst;
import com.junbo.ewallet.db.entity.def.WalletLotType;
import org.hibernate.annotations.Type;

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
public class WalletLotEntity extends Entity {
    private Long walletId;
    private WalletLotType type;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private Date expirationDate;

    @Column(name = "ewallet_id")
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    @Column(name = "type")
    @Type(type = WalletConst.PERSISTED_ENUM_TYPE)
    public WalletLotType getType() {
        return type;
    }

    public void setType(WalletLotType type) {
        this.type = type;
    }

    @Column(name = "total")
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "remaining")
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    @Column(name = "expiration_date")
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
