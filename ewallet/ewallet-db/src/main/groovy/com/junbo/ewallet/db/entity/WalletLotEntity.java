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
import java.util.Date;

/**
 * Entity of WalletLot.
 */
@javax.persistence.Entity
@Table(name = "ewallet_lot")
public class WalletLotEntity extends Entity<WalletLotId> {
    private Long walletId;
    @CloudantSerialize(TypeSerializer.WalletLotTypeSerializer.class)
    @CloudantDeserialize(TypeDeserializer.WalletLotTypeDeserializer.class)
    private WalletLotType type;
    @CloudantDeserialize(BigDecimalFromStringDeserializer.class)
    @CloudantSerialize(ToStringSerializer.class)
    private BigDecimal totalAmount;
    @CloudantDeserialize(BigDecimalFromStringDeserializer.class)
    @CloudantSerialize(ToStringSerializer.class)
    private BigDecimal remainingAmount;
    @CloudantSerialize(DateSerializer.class)
    @CloudantDeserialize(DateDeserializer.class)
    private Date expirationDate;

    private Boolean isActive;

    @Column(name = "ewallet_id")
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    @Column(name = "type")
    @Type(type = IdentifiableType.TYPE)
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

    @Transient
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Transient
    @Override
    public WalletLotId getId() {
        return new WalletLotId(getpId());
    }

    @Override
    public void setId(WalletLotId id) {
        this.setpId(id.getValue());
    }
}
