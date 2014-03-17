/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.sharding.annotations.SeedId

import javax.persistence.*
/**
 * UserEntity model for user password entity table
 */
@Entity
@Table(name = 'user_password')
class UserPasswordEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @SeedId
    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'password_hash')
    private String passwordHash

    @Column(name = 'password_salt')
    private String passwordSalt

    @Column(name = 'password_strength')
    private Short strength

    @Column(name = 'active')
    private Boolean active

    @Column(name = 'change_at_next_login')
    private Boolean changeAtNextLogin

    @Column(name = 'created_by')
    private String createdBy

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = 'created_time')
    private Date createdTime

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

    String getPasswordHash() {
        return passwordHash
    }

    void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash
    }

    String getPasswordSalt() {
        return passwordSalt
    }

    void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt
    }

    Short getStrength() {
        return strength
    }

    void setStrength(Short strength) {
        this.strength = strength
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

    String getCreatedBy() {
        return createdBy
    }

    void setCreatedBy(String createdBy) {
        this.createdBy = createdBy
    }

    Date getCreatedTime() {
        return createdTime
    }

    void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime
    }

    Date getExpiresBy() {
        return expiresBy
    }

    void setExpiresBy(Date expiresBy) {
        this.expiresBy = expiresBy
    }
}
