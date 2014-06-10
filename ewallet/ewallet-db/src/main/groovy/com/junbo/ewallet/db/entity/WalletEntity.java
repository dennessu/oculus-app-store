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
import com.junbo.ewallet.db.entity.def.IdentifiableType;
import com.junbo.ewallet.db.entity.def.TypeDeserializer;
import com.junbo.ewallet.db.entity.def.TypeSerializer;
import com.junbo.ewallet.db.entity.def.WalletId;
import com.junbo.ewallet.spec.def.Status;
import com.junbo.ewallet.spec.def.WalletType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity of Wallet.
 */
@javax.persistence.Entity
@Table(name = "ewallet")
public class WalletEntity extends Entity<WalletId> {
    private UUID trackingUuid;
    private Long userId;
    @CloudantSerialize(TypeSerializer.WalletTypeSerializer.class)
    @CloudantDeserialize(TypeDeserializer.WalletTypeDeserializer.class)
    private WalletType type;
    private Status status;
    private String currency;
    @CloudantDeserialize(BigDecimalFromStringDeserializer.class)
    @CloudantSerialize(ToStringSerializer.class)
    private BigDecimal balance;

    @Column(name = "tracking_uuid")
    @Type(type = "pg-uuid")
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "type")
    @Type(type = IdentifiableType.TYPE)
    public WalletType getType() {
        return type;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    @Column(name = "status")
    @Type(type = IdentifiableType.TYPE)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "balance")
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return userId;
    }

    @Transient
    @Override
    public WalletId getId() {
        return new WalletId(getpId());
    }

    @Override
    public void setId(WalletId id) {
        this.setpId(id.getValue());
    }
}
