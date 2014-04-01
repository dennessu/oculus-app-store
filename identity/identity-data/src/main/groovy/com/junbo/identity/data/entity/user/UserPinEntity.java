/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user;

import com.junbo.identity.data.entity.common.ResourceMetaEntity;
import com.junbo.sharding.annotations.SeedId;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liangfu on 3/16/14.
 */
@Entity
@Table(name = "user_pin")
public class UserPinEntity extends ResourceMetaEntity {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public String getPinSalt() {
        return pinSalt;
    }

    public void setPinSalt(String pinSalt) {
        this.pinSalt = pinSalt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getChangeAtNextLogin() {
        return changeAtNextLogin;
    }

    public void setChangeAtNextLogin(Boolean changeAtNextLogin) {
        this.changeAtNextLogin = changeAtNextLogin;
    }

    public Date getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy;
    }

    @Id
    @Column(name = "id")
    private Long id;
    @SeedId
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "pin_hash")
    private String pinHash;
    @Column(name = "pin_salt")
    private String pinSalt;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "change_at_next_login")
    private Boolean changeAtNextLogin;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_by")
    private Date expiresBy;
}
