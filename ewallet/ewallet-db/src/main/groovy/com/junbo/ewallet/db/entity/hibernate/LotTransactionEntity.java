/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity.hibernate;

import com.junbo.ewallet.db.entity.def.IdentifiableType;
import com.junbo.ewallet.db.entity.def.TransactionType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Entity of LotTransaction.
 */
@javax.persistence.Entity
@Table(name = "lot_transaction")
public class LotTransactionEntity extends EntityWithCreated {
    private Long walletId;
    private Long walletLotId;
    private TransactionType type;
    private BigDecimal amount;

    @Column(name = "ewallet_id")
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    @Column(name = "ewallet_lot_id")
    public Long getWalletLotId() {
        return walletLotId;
    }

    public void setWalletLotId(Long walletLotId) {
        this.walletLotId = walletLotId;
    }

    @Column(name = "type")
    @Type(type = IdentifiableType.TYPE)
    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return walletId;
    }
}
