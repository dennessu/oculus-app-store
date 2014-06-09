/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.junbo.common.cloudant.json.annotations.CloudantDeserialize;
import com.junbo.common.cloudant.json.annotations.CloudantSerialize;
import com.junbo.common.jackson.deserializer.BigDecimalFromStringDeserializer;
import com.junbo.ewallet.db.entity.def.*;
import com.junbo.ewallet.spec.def.WalletLotType;
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
public class LotTransactionEntity extends EntityWithCreated<LotTransactionId> {
    private Long walletId;
    private Long walletLotId;
    private Long transactionId;
    private TransactionType type;
    @CloudantSerialize(TypeSerializer.WalletLotTypeSerializer.class)
    @CloudantDeserialize(TypeDeserializer.WalletLotTypeDeserializer.class)
    private WalletLotType walletLotType;
    @CloudantDeserialize(BigDecimalFromStringDeserializer.class)
    @CloudantSerialize(ToStringSerializer.class)
    private BigDecimal amount;
    @CloudantDeserialize(BigDecimalFromStringDeserializer.class)
    @CloudantSerialize(ToStringSerializer.class)
    private BigDecimal unrefundedAmount;

    private Boolean isRefundEnded;

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

    @Column(name = "transaction_id")
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    @Column(name = "type")
    @Type(type = IdentifiableType.TYPE)
    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    @Column(name = "wallet_lot_type")
    @Type(type = IdentifiableType.TYPE)
    public WalletLotType getWalletLotType() {
        return walletLotType;
    }

    public void setWalletLotType(WalletLotType walletLotType) {
        this.walletLotType = walletLotType;
    }

    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name = "unrefunded_amount")
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

    @Transient
    public Boolean getIsRefundEnded() {
        return isRefundEnded;
    }

    public void setIsRefundEnded(Boolean isRefundEnded) {
        this.isRefundEnded = isRefundEnded;
    }

    @Transient
    @Override
    public LotTransactionId getId() {
        return new LotTransactionId(getpId());
    }

    @Override
    public void setId(LotTransactionId id) {
        this.setpId(id.getValue());
    }
}
