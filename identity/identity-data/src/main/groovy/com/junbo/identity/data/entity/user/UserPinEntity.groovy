/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user
import com.junbo.identity.data.entity.common.ResourceMetaEntity
import com.junbo.sharding.annotations.SeedId
import groovy.transform.CompileStatic

import javax.persistence.*
/**
 * Created by liangfu on 3/16/14.
 */
@Entity
@Table(name = 'user_pin')
@CompileStatic
class UserPinEntity extends ResourceMetaEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @SeedId
    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'pin_hash')
    private String pinHash

    @Column(name = 'pin_salt')
    private String pinSalt

    @Column(name = 'active')
    private Boolean active

    @Column(name = 'change_at_next_login')
    private Boolean changeAtNextLogin

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = 'expires_by')
    private Date expiresBy

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

    String getPinHash() {
        return pinHash
    }

    void setPinHash(String pinHash) {
        this.pinHash = pinHash
    }

    String getPinSalt() {
        return pinSalt
    }

    void setPinSalt(String pinSalt) {
        this.pinSalt = pinSalt
    }

    Boolean getActive() {
        return active
    }

    void setActive(Boolean active) {
        this.active = active
    }

    Boolean getChangeAtNextLogin() {
        return changeAtNextLogin
    }

    void setChangeAtNextLogin(Boolean changeAtNextLogin) {
        this.changeAtNextLogin = changeAtNextLogin
    }

    Date getExpiresBy() {
        return expiresBy
    }

    void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy
    }
}
