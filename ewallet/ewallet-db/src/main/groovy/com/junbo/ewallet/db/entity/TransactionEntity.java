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
import com.junbo.common.util.Identifiable;
import com.junbo.ewallet.db.entity.def.IdentifiableType;
import com.junbo.ewallet.db.entity.def.TransactionId;
import com.junbo.ewallet.db.entity.def.TransactionType;
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
public class TransactionEntity extends EntityWithCreated implements Identifiable<TransactionId> {
    private UUID trackingUuid;
    private Long walletId;
    private TransactionType type;
    @CloudantDeserialize(BigDecimalFromStringDeserializer.class)
    @CloudantSerialize(ToStringSerializer.class)
    private BigDecimal amount;
    private Long offerId;
    @CloudantDeserialize(BigDecimalFromStringDeserializer.class)
    @CloudantSerialize(ToStringSerializer.class)
    private BigDecimal unrefundedAmount;

    @Column(name = "tracking_uuid")
    @Type(type = "pg-uuid")
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @Column(name = "ewallet_id")
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
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

    public void setAmount(BigDecimal mount) {
        this.amount = mount;
    }

    @Column(name = "unrefunded_amount")
    public BigDecimal getUnrefundedAmount() {
        return unrefundedAmount;
    }

    public void setUnrefundedAmount(BigDecimal unrefundedAmount) {
        this.unrefundedAmount = unrefundedAmount;
    }

    @Column(name = "offer_id")
    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return walletId;
    }

    @Transient
    @Override
    public TransactionId getId() {
        return new TransactionId(getpId());
    }

    @Override
    public void setId(TransactionId id) {
        this.setpId(id.getValue());
    }
}
