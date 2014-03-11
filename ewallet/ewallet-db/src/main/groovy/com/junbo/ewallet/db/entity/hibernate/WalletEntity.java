/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity.hibernate;

import com.junbo.ewallet.db.entity.def.Currency;
import com.junbo.ewallet.db.entity.def.IdentifiableType;
import com.junbo.ewallet.db.entity.def.Status;
import com.junbo.ewallet.db.entity.def.WalletType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Entity of Wallet.
 */
@javax.persistence.Entity
@Table(name = "ewallet")
public class WalletEntity extends Entity {
    private Long userId;
    private WalletType type;
    private Status status;
    private Currency currency;
    private BigDecimal balance;

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
    @Type(type = IdentifiableType.TYPE)
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
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

}
