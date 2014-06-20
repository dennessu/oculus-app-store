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

/**
 * Entity of LotTransaction.
 */
@javax.persistence.Entity
@Table(name = "lot_transaction")
public class LotTransactionEntity extends BaseEntity {
    @Column(name = "ewallet_id")
    private Long walletId;

    @Column(name = "ewallet_lot_id")
    private Long walletLotId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "wallet_lot_type_id")
    private Integer walletLotTypeId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "unrefunded_amount")
    private BigDecimal unrefundedAmount;

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Long getWalletLotId() {
        return walletLotId;
    }

    public void setWalletLotId(Long walletLotId) {
        this.walletLotId = walletLotId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getWalletLotTypeId() {
        return walletLotTypeId;
    }

    public void setWalletLotTypeId(Integer walletLotTypeId) {
        this.walletLotTypeId = walletLotTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
